package cloud.weareithero.api.outbound.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cloud.weareithero.api.outbound.dto.OutboundCarrierDTO;
import cloud.weareithero.api.outbound.dto.OutboundDTO;
import cloud.weareithero.api.outbound.dto.OutboundManifestDTO;
import cloud.weareithero.api.outbound.dto.OutboundPackingDTO;
import cloud.weareithero.api.outbound.dto.OutboundProductDTO;
import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;
import cloud.weareithero.api.outbound.dto.OutboundSummaryDTO;
import cloud.weareithero.api.outbound.dto.OutboundTransportationVehicleDTO;
import cloud.weareithero.api.outbound.dto.OutboundVehicleAssignDTO;

@Mapper
public interface OutboundMapper {

  /**
   * 1. 출고 화면 상단 집계 카드 5종
   * 기준 테이블: OUTBOUND (JOIN ORDER_PRODUCT_LIST 없음 - 3차안 기준 OUTBOUND 직접
   * partner_company_id 보유)
   *
   * 집계 항목:
   * total - 전체 OUTBOUND 건수
   * expectedToday - 당일 etd 기준 미완료 건 (OUTBOUND.etd = CURDATE(), state_code != 완료)
   * confirmedToday- OUTBOUND_TRANSPORTATION.atd 기준 당일 출고 완료 건
   * (OUTBOUND_PACKING → OUTBOUND_TRANSPORTATION 경유 서브쿼리)
   * nearDeadline - OUTBOUND.deadline 기준 3일 이내 미완료 건
   * TODO: 기한 임박 기준 일수(현재 3일) 팀 협의 후 확정 필요
   * overdue - OUTBOUND.deadline < CURDATE() 미완료 건
   * unassigned - OUTBOUND_PACKING.outbound_transportation_id IS NULL 건
   *
   * 동적 조건:
   * outboundId - OUTBOUND.id LIKE
   * customerName - PARTNER_COMPANY_MASTER.name LIKE
   * stateCode - OUTBOUND.state_code =
   * orderStart/orderEnd - OUTBOUND.order_date BETWEEN
   *
   * TODO: '완료' state_code 정수값 확인 필요 (현재 CASE WHEN state_code != 9 로 임시 처리)
   * OrderMapper 기준 9 = 출고완료 이므로 동일 체계라면 9 사용 가능, COMMON_CODE 확인 필요
   */
  @Select("<script>" +
      """
          SELECT
            COUNT(DISTINCT `ob`.`id`) AS `total`,
            SUM(CASE WHEN DATE(`ob`.`etd`) = CURDATE()
                          AND `ob`.`state_code` != 9
                     THEN 1 ELSE 0 END) AS `expectedToday`,
            SUM(CASE WHEN EXISTS (
                       SELECT 1
                       FROM `OUTBOUND_PACKING` `op2`
                       JOIN `OUTBOUND_TRANSPORTATION` `ot2`
                         ON `op2`.`outbound_transportation_id` = `ot2`.`id`
                       WHERE `op2`.`outbound_id` = `ob`.`id`
                         AND DATE(`ot2`.`atd`) = CURDATE()
                     ) THEN 1 ELSE 0 END) AS `confirmedToday`,
            SUM(CASE WHEN DATEDIFF(`ob`.`deadline`, CURDATE()) BETWEEN 0 AND 3
                          AND `ob`.`state_code` != 9
                     THEN 1 ELSE 0 END) AS `nearDeadline`,
            SUM(CASE WHEN `ob`.`deadline` &lt; CURDATE()
                          AND `ob`.`state_code` != 9
                     THEN 1 ELSE 0 END) AS `overdue`,
            SUM(CASE WHEN EXISTS (
                       SELECT 1
                       FROM `OUTBOUND_PACKING` `op3`
                       WHERE `op3`.`outbound_id` = `ob`.`id`
                         AND `op3`.`outbound_transportation_id` IS NULL
                     ) THEN 1 ELSE 0 END) AS `unassigned`
          FROM `OUTBOUND` `ob`
          JOIN `PARTNER_COMPANY_MASTER` `pcm`
            ON `ob`.`partner_company_id` = `pcm`.`id`
          """ +
      "<where>" +
      "<if test='orderStart != null and orderStart != \"\" and orderEnd != null and orderEnd != \"\"'>" +
      " AND `ob`.`order_date` BETWEEN #{orderStart} AND #{orderEnd} " +
      "</if>" +
      "<if test='outboundId != null and outboundId != 0'>" +
      " AND `ob`.`id` LIKE CONCAT('%', #{outboundId}, '%') " +
      "</if>" +
      "<if test='customerName != null and customerName != \"\"'>" +
      " AND `pcm`.`name` LIKE CONCAT('%', #{customerName}, '%') " +
      "</if>" +
      "<if test='stateCode != null and stateCode != 0'>" +
      " AND `ob`.`state_code` = #{stateCode} " +
      "</if>" +
      "</where>" +
      "</script>")
  public OutboundSummaryDTO findSummary(OutboundRequestDTO outboundRequestDTO);

  /**
   * 2. 박스 뷰 목록 조회 (OUTBOUND_PACKING 기준, 페이지네이션 포함)
   *
   * JOIN 경로:
   * OUTBOUND_PACKING op
   * → OUTBOUND ob ON op.outbound_id = ob.id
   * → PARTNER_COMPANY_MASTER pcm ON ob.partner_company_id = pcm.id (고객사)
   * → PARTNER_COMPANY_MASTER carrier ON op.partner_company_id = carrier.id (운송사,
   * LEFT JOIN)
   * ※ OUTBOUND_PACKING.partner_company_id = 운송사 FK (DB 설계서 확인됨)
   * → OUTBOUND_TRANSPORTATION ot ON op.outbound_transportation_id = ot.id (LEFT
   * JOIN)
   * → COMMON_CODE cc ON op.state_code = cc.id
   *
   * DTO alias 매핑:
   * packingId ← op.id
   * packingInvoiceNumber ← op.packing_invoice_number (박스번호)
   * outboundId ← op.outbound_id (주문번호)
   * customerName ← pcm.name
   * carrierName ← carrier.name (미배정 시 NULL)
   * invoiceNumber ← op.invoice_number (송장 발급 전 NULL)
   * stateCode ← op.state_code
   * stateName ← cc.name
   * deadline ← ob.deadline
   * etd ← ob.etd
   * transportationId ← op.outbound_transportation_id (미배정 시 NULL)
   *
   * 동적 조건: Inbound findAll 패턴 동일 적용
   * ORDER BY op.id DESC, LIMIT #{offset}, #{size}
   */
  /**
   * 2. 박스 뷰 목록 조회 (OUTBOUND_PACKING 기준, 페이지네이션 포함)
   */
  @Select("<script>" +
      """
            SELECT
              `op`.`id`                           AS `packingId`,
              `op`.`packing_invoice_number`        AS `packingInvoiceNumber`,
              `op`.`outbound_id`                   AS `outboundId`,
              `pcm`.`name`                         AS `customerName`,
              `carrier`.`id`                       AS `carrierId`,
              `carrier`.`name`                     AS `carrierName`,
              `op`.`invoice_number`                AS `invoiceNumber`,
              `op`.`state_code`                    AS `stateCode`,
              CASE WHEN `op`.`state_code` = 20 THEN '차량미배정' ELSE `cc`.`name` END AS `stateName`,
              `ob`.`deadline`                      AS `deadline`,
              `ob`.`etd`                           AS `etd`,
              `op`.`outbound_transportation_id`    AS `transportationId`
            FROM `OUTBOUND_PACKING` `op`
            JOIN `OUTBOUND` `ob`
              ON `op`.`outbound_id` = `ob`.`id`
            JOIN `PARTNER_COMPANY_MASTER` `pcm`
              ON `ob`.`partner_company_id` = `pcm`.`id`
            LEFT JOIN `OUTBOUND_TRANSPORTATION` `ot`
              ON `op`.`outbound_transportation_id` = `ot`.`id`
            LEFT JOIN `PARTNER_COMPANY_MASTER` `carrier`
              ON `ot`.`partner_company_id` = `carrier`.`id`
            JOIN `COMMON_CODE` `cc`
              ON `op`.`state_code` = `cc`.`id`
          """
      +
      "<where>" +
      " AND `op`.`state_code` = 20 " +
      "<if test='customerName != null and customerName != \"\"'>" +
      " AND `pcm`.`name` LIKE CONCAT('%', #{customerName}, '%') " +
      "</if>" +
      "<if test='orderStart != null and orderStart != \"\" and orderEnd != null and orderEnd != \"\"'>" +
      " AND `ob`.`order_date` BETWEEN #{orderStart} AND #{orderEnd} " +
      "</if>" +
      "</where>" +
      "ORDER BY `op`.`id` DESC LIMIT #{offset}, #{size}" +
      "</script>")
  public List<OutboundPackingDTO> findAll(OutboundRequestDTO outboundRequestDTO);

  /**
   * 3. OUTBOUND_PACKING 단건 조회 (issueInvoice 상태 가드용)
   *
   * JOIN: COMMON_CODE (state_code 명칭)
   * WHERE OUTBOUND_PACKING.id = #{packingId}
   */
  @Select("""
      SELECT
        `op`.`id`                              AS `packingId`,
        `op`.`packing_invoice_number`          AS `packingInvoiceNumber`,
        `op`.`outbound_id`                     AS `outboundId`,
        `op`.`invoice_number`                  AS `invoiceNumber`,
        `op`.`state_code`                      AS `stateCode`,
        `cc`.`name`                            AS `stateName`,
        `op`.`outbound_transportation_id`      AS `transportationId`
      FROM `OUTBOUND_PACKING` `op`
      JOIN `COMMON_CODE` `cc`
        ON `op`.`state_code` = `cc`.`id`
      WHERE `op`.`id` = #{packingId}
      """)
  public OutboundPackingDTO findByPackingId(int packingId);

  /**
   * 4. OUTBOUND 단건 조회 (상세 모달 헤더용)
   *
   * JOIN 경로:
   * OUTBOUND ob
   * → PARTNER_COMPANY_MASTER pcm ON ob.partner_company_id = pcm.id (고객사)
   * → COMMON_CODE cc ON ob.state_code = cc.id
   *
   * DTO alias 매핑:
   * outboundId ← ob.id
   * partnerId ← ob.partner_company_id
   * partnerName ← pcm.name
   * orderDate ← ob.order_date
   * deadline ← ob.deadline
   * etd ← ob.etd
   * stateCode ← ob.state_code
   * stateName ← cc.name
   */
  @Select("""
      SELECT
        `ob`.`id`                    AS `outboundId`,
        `ob`.`partner_company_id`    AS `partnerId`,
        `pcm`.`name`                 AS `partnerName`,
        `ob`.`order_date`            AS `orderDate`,
        `ob`.`deadline`              AS `deadline`,
        `ob`.`etd`                   AS `etd`,
        `ob`.`state_code`            AS `stateCode`,
        `cc`.`name`                  AS `stateName`
      FROM `OUTBOUND` `ob`
      JOIN `PARTNER_COMPANY_MASTER` `pcm`
        ON `ob`.`partner_company_id` = `pcm`.`id`
      LEFT JOIN `COMMON_CODE` `cc`
        ON `ob`.`state_code` = `cc`.`id`
      WHERE `ob`.`id` = #{outboundId}
      """)
  public OutboundDTO findByOutboundId(int outboundId);

  /**
   * 5. ORDER_PRODUCT 목록 조회 (상세 모달 제품 목록)
   *
   * JOIN 경로:
   * ORDER_PRODUCT op
   * → OUTBOUND_PRODUCT_MASTER opm ON op.outbound_product_id = opm.id
   *
   * WHERE op.outbound_id = #{outboundId}
   * ※ 3차안 기준 ORDER_PRODUCT.outbound_id → OUTBOUND.id 직접 연결
   * (이전 설계의 order_product_list_id 사용 안 함)
   *
   * DTO alias 매핑:
   * productId ← op.id
   * productName ← opm.name
   * quantity ← op.quantity
   * price ← op.price
   * totalPrice ← op.total_price
   */
  @Select("""
      SELECT
        `op`.`id`            AS `productId`,
        `opm`.`name`         AS `productName`,
        `op`.`quantity`      AS `quantity`,
        `op`.`price`         AS `price`,
        `op`.`total_price`   AS `totalPrice`
      FROM `ORDER_PRODUCT` `op`
      JOIN `OUTBOUND_PRODUCT_MASTER` `opm`
        ON `op`.`outbound_product_id` = `opm`.`id`
      WHERE `op`.`outbound_id` = #{outboundId}
      """)
  public List<OutboundProductDTO> findProducts(int outboundId);

  /**
   * 6. 매니페스트 뷰 목록 조회 (OUTBOUND_TRANSPORTATION 기준, 페이지네이션 포함)
   *
   * JOIN 경로:
   * OUTBOUND_TRANSPORTATION ot
   * → PARTNER_COMPANY_MASTER carrier ON ot.partner_company_id = carrier.id
   * → TRANSPORTATION_VEHICLE_MASTER tvm ON ot.transportation_vehicle_id = tvm.id
   * → OUTBOUND_PACKING op ON op.outbound_transportation_id = ot.id (LEFT JOIN)
   * → OUTBOUND ob ON op.outbound_id = ob.id (LEFT JOIN, 필터용)
   * → PARTNER_COMPANY_MASTER pcm ON ob.partner_company_id = pcm.id (LEFT JOIN,
   * customerName 필터용)
   * → COMMON_CODE cc ON ot.state_code = cc.id
   *
   * GROUP BY ot.id (OUTBOUND_PACKING COUNT 집계)
   *
   * DTO alias 매핑:
   * transportationId ← ot.id
   * manifestNo ← CONCAT('MNF-', LPAD(ot.id, 6, '0')) (별도 컬럼 없음, 가공)
   * carrierName ← carrier.name
   * vehicleInfo ← CONCAT(ot.lpn, ' (', tvm.vehicle_type, ')')
   * boxCount ← COUNT(op.id)
   * stateCode ← ot.state_code
   * stateName ← cc.name
   * etd ← ot.etd
   * atd ← ot.atd
   * driver ← ot.driver
   *
   * 동적 조건:
   * stateCode - ot.state_code = (HAVING 대신 WHERE 적용 - GROUP 전 필터)
   * customerName - pcm.name LIKE (LEFT JOIN 후 WHERE 적용)
   * orderStart/orderEnd - ob.order_date BETWEEN (LEFT JOIN 후 WHERE 적용)
   * ORDER BY ot.id DESC, LIMIT #{offset}, #{size}
   */

  // OutboundMapper.java - 6. 매니페스트 뷰 목록 조회 수정
  @Select("<script>" +
      """
            SELECT
              `ot`.`id`                                                          AS `transportationId`,
              CONCAT('MNF-', LPAD(`ot`.`id`, 6, '0'))                           AS `manifestNo`,
              `carrier`.`name`                                                   AS `carrierName`,
              CONCAT(`ot`.`lpn`, ' (', `tvm`.`vehicle_type`, ')')               AS `vehicleInfo`,
              COUNT(`op`.`id`)                                                   AS `boxCount`,
              `ot`.`state_code`                                                  AS `stateCode`,
              `cc`.`name`                                                        AS `stateName`,
              `ot`.`etd`                                                         AS `etd`,
              `ot`.`atd`                                                         AS `atd`,
              `ot`.`driver`                                                      AS `driver`
            FROM `OUTBOUND_TRANSPORTATION` `ot`
            JOIN `PARTNER_COMPANY_MASTER` `carrier` ON `ot`.`partner_company_id` = `carrier`.`id`
            JOIN `TRANSPORTATION_VEHICLE_MASTER` `tvm` ON `ot`.`transportation_vehicle_id` = `tvm`.`id`
            LEFT JOIN `OUTBOUND_PACKING` `op` ON `op`.`outbound_transportation_id` = `ot`.`id`
            LEFT JOIN `OUTBOUND` `ob` ON `op`.`outbound_id` = `ob`.`id`
            LEFT JOIN `PARTNER_COMPANY_MASTER` `pcm` ON `ob`.`partner_company_id` = `pcm`.`id`
            JOIN `COMMON_CODE` `cc` ON `ot`.`state_code` = `cc`.`id`
          """ +
      "<where>" +
      "   `ot`.`state_code` IS NOT NULL " +
      "<if test='stateCode != null and stateCode != 0'>" +
      " AND `ot`.`state_code` = #{stateCode} " +
      "</if>" +
      "<if test='customerName != null and customerName != \"\"'>" +
      " AND `pcm`.`name` LIKE CONCAT('%', #{customerName}, '%') " +
      "</if>" +
      "<if test='orderStart != null and orderStart != \"\" and orderEnd != null and orderEnd != \"\"'>" +
      " AND `ob`.`order_date` BETWEEN #{orderStart} AND #{orderEnd} " +
      "</if>" +
      "</where>" +
      "GROUP BY `ot`.`id`, `carrier`.`name`, `ot`.`lpn`, `tvm`.`vehicle_type`, " +
      "`ot`.`state_code`, `cc`.`name`, `ot`.`etd`, `ot`.`atd`, `ot`.`driver` " +
      "ORDER BY `ot`.`id` DESC LIMIT #{offset}, #{size}" +
      "</script>")
  public List<OutboundManifestDTO> findAllManifest(OutboundRequestDTO outboundRequestDTO);

  /**
   * 7. 운송사 목록 조회 (차량 배정 모달 드롭다운)
   * PARTNER_COMPANY_MASTER WHERE carrier_yn_code = 1
   * Inbound의 findBySupplier (supplier_yn_code = 1) 대칭 구조
   */
  @Select("""
      SELECT
        `id`,
        `name`
      FROM `PARTNER_COMPANY_MASTER`
      WHERE `carrier_yn_code` = 1
      """)
  public List<OutboundCarrierDTO> findByCarrier();

  /**
   * 8. 차량 목록 조회 (차량 배정 모달 드롭다운)
   * TRANSPORTATION_VEHICLE_MASTER 전체 조회
   * ※ 차량 번호판(lpn)은 이 테이블에 없음 → 모달에서 직접 입력 → VehicleAssignDTO.lpn 으로 전달
   */
  @Select("""
      SELECT
        `id`,
        `vehicle_type`    AS `vehicleType`,
        `max_payload_ton` AS `maxPayloadTon`
      FROM `TRANSPORTATION_VEHICLE_MASTER`
      """)
  public List<OutboundTransportationVehicleDTO> findByVehicle();

  /**
   * 9. 차량 배정 - OUTBOUND_TRANSPORTATION INSERT
   *
   * 저장 컬럼:
   * partner_company_id ← carrierId
   * transportation_vehicle_id ← vehicleId
   * lpn ← lpn (차량번호 직접 입력)
   * driver ← driver (기사명)
   * etd ← etd (예상 출고 일자)
   * state_code ← stateCode (차량 배정 후 상태 코드)
   * updated_at ← NOW()
   *
   * @Options(useGeneratedKeys = true, keyProperty = "transportationId")
   *                           → 생성된 PK를 OutboundVehicleAssignDTO.transportationId
   *                           에 자동 주입
   *                           → DaoImp.addTransportation() 에서
   *                           dto.getTransportationId() 로 읽어 반환
   */
  @Insert("""
      INSERT INTO `OUTBOUND_TRANSPORTATION` (
        `partner_company_id`,
        `transportation_vehicle_id`,
        `lpn`,
        `driver`,
        `etd`,
        `state_code`,
        `updated_at`
      ) VALUES (
        #{carrierId},
        #{vehicleId},
        #{lpn},
        #{driver},
        #{etd},
        21,
        NOW()
      )
      """)
  @Options(useGeneratedKeys = true, keyProperty = "transportationId", keyColumn = "id")
  public int addTransportation(OutboundVehicleAssignDTO outboundVehicleAssignDTO);

  /**
   * 10. 차량 배정 - OUTBOUND_PACKING UPDATE
   * 선택된 박스(OUTBOUND_PACKING.id)에 transportation_id 연결 + 상태 변경
   *
   * SET outbound_transportation_id = #{transportationId}
   * state_code = #{stateCode}
   * updated_at = NOW()
   * WHERE id = #{packingId}
   *
   * @Param 으로 두 파라미터 바인딩
   *        TODO: state_code 값 COMMON_CODE 확인 필요
   */
  @Update("""
      UPDATE `OUTBOUND_PACKING`
      SET
        `outbound_transportation_id` = #{transportationId},
        `state_code`                 = #{shippingReadyCode},
        `updated_at`                 = NOW()
      WHERE `id` = #{packingId}
      """)
  public int updatePackingTransportation(@Param("packingId") int packingId,
      @Param("transportationId") int transportationId,
      @Param("shippingReadyCode") int shippingReadyCode);

  /**
   * 11. 송장 발급 - OUTBOUND_PACKING UPDATE
   * invoice_number 저장 + 상태 변경
   *
   * SET invoice_number = #{invoiceNumber} (ServiceImp에서 채번 후 DTO에 주입)
   * state_code = TODO: 송장발급완료 state_code 값 COMMON_CODE 확인 후 교체 (현재 임시 0)
   * updated_at = NOW()
   * WHERE id = #{packingId} (ServiceImp 루프 내 단건 처리)
   *
   * [컬럼 구분]
   * invoice_number = 운송장번호 (이 메서드에서 저장)
   * packing_invoice_number = 패킹 박스 식별번호 (기존 값, 변경하지 않음)
   */
  @Update("""
      UPDATE `OUTBOUND_PACKING`
      SET
        `invoice_number` = #{invoiceNumber},
        `state_code`     = #{invoiceStateCode},
        `updated_at`     = NOW()
      WHERE `id` = #{packingId}
      """)
  public int updateInvoiceNumber(@Param("packingId") int packingId,
      @Param("invoiceNumber") String invoiceNumber,
      @Param("invoiceStateCode") int invoiceStateCode); // 💡 DTO 대신 명확하게 파라미터 분리

  /**
   * 12. 출고 확정 - OUTBOUND_TRANSPORTATION UPDATE
   * 매니페스트 단위 출고 확정 처리
   *
   * SET state_code = #{stateCode} (ServiceImp에서 출고완료 코드 전달)
   * atd = NOW() (실제 출고 일시 기록)
   * updated_at = NOW()
   * WHERE id = #{transportationId}
   *
   * @Param 으로 두 파라미터 바인딩
   *        TODO: stateCode = 출고완료 COMMON_CODE id 값 확인 후 ServiceImp 상수 교체 필요
   */
  @Update("""
      UPDATE `OUTBOUND_TRANSPORTATION`
      SET
        `state_code`  = #{stateCode},
        `atd`         = NOW(),
        `updated_at`  = NOW()
      WHERE `id` = #{transportationId}
      """)
  public int updateTransportationConfirm(@Param("transportationId") int transportationId,
      @Param("stateCode") int stateCode);

}