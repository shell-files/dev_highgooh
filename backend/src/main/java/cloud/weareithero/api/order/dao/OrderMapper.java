package cloud.weareithero.api.order.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cloud.weareithero.api.order.dto.OrderCustomerDTO;
import cloud.weareithero.api.order.dto.OrderDTO;
import cloud.weareithero.api.order.dto.OrderDetailProductDTO;
import cloud.weareithero.api.order.dto.OrderProductDTO;
import cloud.weareithero.api.order.dto.OrderRequestDTO;
import cloud.weareithero.api.order.dto.OrderSummaryDTO;

/**
 * OrderMapper - MyBatis Annotation 방식 (XML 미사용)
 * Inbound AsnMapper 스타일 완전 동일 유지
 *
 * [DB 변경] 3차안 기준 테이블 구조:
 * 주문 헤더 : OUTBOUND (id, partner_company_id, order_date, deadline, etd,
 * state_code, updated_at)
 * 주문 품목 : ORDER_PRODUCT (id, outbound_id, outbound_product_id, quantity, price,
 * total_price)
 * 고객사 : PARTNER_COMPANY_MASTER (customer_yn_code = 1)
 * 완제품 : OUTBOUND_PRODUCT_MASTER
 * 상태코드 : COMMON_CODE (state_code FK)
 */
@Mapper
public interface OrderMapper {

  // ──────────────────────────────────────────────────────────────
  // 1. 주문 요약 카드 집계 (상단 4개 카드)
  // OrderSummaryDTO: total / newOrder / inProgress / completed
  //
  // [DB 변경] 테이블: ORDER_PRODUCT_LIST → OUTBOUND
  // [DB 변경] AS 별칭: OUTBOUND 컬럼명에 맞게 전면 재작성
  //
  // TODO: COMMON_CODE에서 OUTBOUND 진행상태 코드값 확인 후
  // 아래 state_code 숫자(신규=?, 처리중=?, 완료=?) 교체 필요
  // 현재 임시값으로 주석 처리된 TODO 값 확인 후 반드시 수정
  // ──────────────────────────────────────────────────────────────
  @Select("<script>" +
      """
          SELECT
            COUNT(*) AS `total`,
            SUM(CASE WHEN `o`.`state_code` = 7 THEN 1 ELSE 0 END) AS `newOrder`,
            SUM(CASE WHEN `o`.`state_code` = 8 THEN 1 ELSE 0 END) AS `inProgress`,
            SUM(CASE WHEN `o`.`state_code` = 9 THEN 1 ELSE 0 END) AS `completed`
          FROM `OUTBOUND` `o`
          JOIN `PARTNER_COMPANY_MASTER` `pcm`
            ON `o`.`partner_company_id` = `pcm`.`id`
          JOIN `COMMON_CODE` `cc`
            ON `o`.`state_code` = `cc`.`id`
          """ +
      "<where>" +
      "<if test='orderStart != null and orderStart != \"\" and orderEnd != null and orderEnd != \"\"'>" +
      " AND `o`.`order_date` BETWEEN STR_TO_DATE(#{orderStart}, '%Y-%m-%d') AND STR_TO_DATE(#{orderEnd}, '%Y-%m-%d') " +
      "</if>" +
      "<if test='outboundId != null and outboundId != \"\"'>" +
      " AND `o`.`id` LIKE CONCAT('%', #{outboundId}, '%') " +
      "</if>" +
      "<if test='customerName != null and customerName != \"\"'>" +
      " AND `pcm`.`name` LIKE CONCAT('%', #{customerName}, '%') " +
      "</if>" +

      // 💡 동적 상태 코드 필터링 추가 (상태 탭 연동)
      "<choose>" +
      "  <when test='status != null and status != \"\"'>" +
      "    AND `cc`.`name` = #{status} " + // 특정 상태("신규", "처리중", "주문완료") 선택 시
      "  </when>" +
      "  <otherwise>" +
      "    AND `o`.`state_code` BETWEEN 7 AND 9 " + // '전체' 선택 시 기본 노출 범위 제한
      "  </otherwise>" +
      "</choose>" +

      "</where>" +
      "</script>")
  public OrderSummaryDTO findSummary(OrderRequestDTO orderRequestDTO);

  // ──────────────────────────────────────────────────────────────
  // 2. 주문 목록 조회 (페이지네이션 + 필터)
  // 화면 컬럼: 주문번호 / 고객사명 / 총 공급가액 / 주문일자 / 출고마감 / 진행상태
  //
  // [DB 변경] 테이블: ORDER_PRODUCT_LIST → OUTBOUND
  // [DB 변경] 총 공급가액: ORDER_PRODUCT.total_price SUM 서브쿼리
  // [DB 변경] 상태명: COMMON_CODE.name AS stateCode
  // [DB 변경] outboundId 기준 LIKE 검색
  //
  // OrderDTO 필드 매핑:
  // outboundId ← o.id
  // partnerCompanyId← o.partner_company_id
  // partnerName ← pcm.name
  // orderDate ← o.order_date
  // deadline ← o.deadline
  // totalPrice ← SUM(op.total_price) 서브쿼리
  // stateCode ← cc.name
  // ──────────────────────────────────────────────────────────────
  @Select("<script>" +
      """
          SELECT
            `o`.`id`                       AS `outboundId`,
            `o`.`partner_company_id`       AS `partnerCompanyId`,
            `pcm`.`name`                   AS `partnerName`,
            `o`.`order_date`               AS `orderDate`,
            `o`.`deadline`                 AS `deadline`,
            (SELECT SUM(CAST(`op`.`total_price` AS UNSIGNED))
             FROM `ORDER_PRODUCT` `op`
             WHERE `op`.`outbound_id` = `o`.`id`) AS `totalPrice`,
            `cc`.`name`                    AS `stateCode`
          FROM `OUTBOUND` `o`
          JOIN `PARTNER_COMPANY_MASTER` `pcm`
            ON `o`.`partner_company_id` = `pcm`.`id`
          LEFT JOIN `COMMON_CODE` `cc`
            ON `o`.`state_code` = `cc`.`id`
          """ +
      "<where>" +
      "<if test='orderStart != null and orderStart != \"\" and orderEnd != null and orderEnd != \"\"'>" +
      " AND `o`.`order_date` BETWEEN STR_TO_DATE(#{orderStart}, '%Y-%m-%d') AND STR_TO_DATE(#{orderEnd}, '%Y-%m-%d') " +
      "</if>" +
      "<if test='outboundId != null and outboundId != \"\"'>" +
      " AND `o`.`id` LIKE CONCAT('%', #{outboundId}, '%') " +
      "</if>" +
      "<if test='customerName != null and customerName != \"\"'>" +
      " AND `pcm`.`name` LIKE CONCAT('%', #{customerName}, '%') " +
      "</if>" +

      // 💡 동적 상태 코드 필터링 추가 (상태 탭 연동)
      "<choose>" +
      "  <when test='status != null and status != \"\"'>" +
      "    AND `cc`.`name` = #{status} " + // 특정 상태 이름이 들어왔을 때 해당 데이터만 조회
      "  </when>" +
      "  <otherwise>" +
      "    AND `o`.`state_code` BETWEEN 7 AND 9 " + // 아무것도 없거나 '전체'일 때 7, 8, 9만 묶어서 조회
      "  </otherwise>" +
      "</choose>" +

      "</where>" +
      "ORDER BY `o`.`id` DESC LIMIT #{offset}, #{size}" +
      "</script>")
  public List<OrderDTO> findAll(OrderRequestDTO orderRequestDTO);

  // ──────────────────────────────────────────────────────────────
  // 3. 주문 마스터 단건 조회 (상세 모달 헤더)
  // [DB 변경] ORDER_PRODUCT_LIST → OUTBOUND
  // [DB 변경] 파라미터명: orderId → outboundId
  // ──────────────────────────────────────────────────────────────
  @Select("""
      SELECT
        `o`.`id`                   AS `outboundId`,
        `o`.`partner_company_id`   AS `partnerCompanyId`,
        `pcm`.`name`               AS `partnerName`,
        `o`.`order_date`           AS `orderDate`,
        `o`.`deadline`             AS `deadline`,
        `cc`.`name`                AS `stateCode`
      FROM `OUTBOUND` `o`
      JOIN `PARTNER_COMPANY_MASTER` `pcm`
        ON `o`.`partner_company_id` = `pcm`.`id`
      LEFT JOIN `COMMON_CODE` `cc`
        ON `o`.`state_code` = `cc`.`id`
      WHERE `o`.`id` = #{outboundId}
      """)
  public OrderDTO findByOutboundId(int outboundId);

  // ──────────────────────────────────────────────────────────────
  // 4. 주문 품목 상세 조회 (상세 모달 테이블)
  // [DB 변경] ORDER_PRODUCT.outbound_id FK
  // [DB 변경] 컬럼: quantity, price, total_price (DB 3차안 기준)
  // OrderDetailProductDTO 필드 매핑:
  // no ← op.id
  // outboundId ← op.outbound_id
  // outboundProductId ← op.outbound_product_id
  // productName ← opm.name
  // quantity ← op.quantity
  // price ← op.price
  // totalPrice ← op.total_price
  // ──────────────────────────────────────────────────────────────
  @Select("""
      SELECT
        `op`.`id`                     AS `no`,
        `op`.`outbound_id`            AS `outboundId`,
        `op`.`outbound_product_id`    AS `outboundProductId`,
        `opm`.`name`                  AS `productName`,
        `op`.`quantity`               AS `quantity`,
        `op`.`price`                  AS `price`,
        `op`.`total_price`            AS `totalPrice`
      FROM `ORDER_PRODUCT` `op`
      JOIN `OUTBOUND_PRODUCT_MASTER` `opm`
        ON `op`.`outbound_product_id` = `opm`.`id`
      WHERE `op`.`outbound_id` = #{outboundId}
      """)
  public List<OrderDetailProductDTO> findOne(int outboundId);

  // ──────────────────────────────────────────────────────────────
  // 5. 주문 마스터 INSERT (OUTBOUND 테이블)
  // [DB 변경] 테이블: ORDER_PRODUCT_LIST → OUTBOUND
  // [DB 변경] keyProperty: orderId → outboundId
  // 💡 [수정완료] 빠져있던 etd, state_code, total_quantity 컬럼 및 매핑 추가
  // ──────────────────────────────────────────────────────────────
  @Insert("""
      INSERT INTO `OUTBOUND` (
        `partner_company_id`,
        `order_date`,
        `deadline`,
        `etd`,
        `state_code`,
        `total_quantity`
      ) VALUES (
        #{partnerCompanyId},
        #{orderDate},
        #{deadline},
        #{etd},
        #{stateCode},
        #{totalQuantity}
      )
      """)
  @Options(useGeneratedKeys = true, keyProperty = "outboundId", keyColumn = "id")
  public int add(OrderDTO orderDTO);

  // ──────────────────────────────────────────────────────────────
  // 6. 주문 품목 INSERT (ORDER_PRODUCT 테이블)
  // [DB 변경] 테이블: ORDER_PRODUCT (outbound_id FK)
  // [DB 변경] price, total_price 컬럼 모두 포함
  // ──────────────────────────────────────────────────────────────
  @Insert("""
      INSERT INTO `ORDER_PRODUCT` (
        `outbound_id`,
        `outbound_product_id`,
        `quantity`,
        `price`,
        `total_price`
      ) VALUES (
        #{outboundId},
        #{outboundProductId},
        #{quantity},
        #{price},
        #{totalPrice}
      )
      """)
  public int addOrderProduct(OrderDetailProductDTO orderDetailProductDTO);

  // ──────────────────────────────────────────────────────────────
  // 7. 주문 마스터 UPDATE (OUTBOUND 테이블)
  // [신규] Inbound에 없던 수정 기능
  // deadline, updated_at만 수정 (state_code는 별도 상태 변경 API 검토 여지 있음)
  //
  // TODO: state_code 수정도 이 API에서 처리할지 별도 상태변경 API로 분리할지 결정 필요
  // ──────────────────────────────────────────────────────────────
  @Update("""
      UPDATE `OUTBOUND`
      SET
        `deadline`   = #{deadline},
        `updated_at` = NOW()
      WHERE `id` = #{outboundId}
      """)
  public int update(OrderDTO orderDTO);

  // ──────────────────────────────────────────────────────────────
  // 8. 주문 품목 전체 삭제 (outbound_id 기준)
  // [신규] 수정 시 재삽입 전 기존 품목 정리
  // 삭제 단독 API에서도 호출됨 (FK 제약 순서)
  // ──────────────────────────────────────────────────────────────
  // @Delete("""
  // DELETE FROM `ORDER_PRODUCT`
  // WHERE `outbound_id` = #{outboundId}
  // """)
  // public int deleteOrderProducts(int outboundId);

  // ──────────────────────────────────────────────────────────────
  // 9. 주문 마스터 삭제 (OUTBOUND 테이블)
  // [신규] ORDER_PRODUCT 삭제 후 호출 (FK 순서 준수)
  // TODO: OUTBOUND_PACKING, OUTBOUND_TRANSPORT 등 하위 테이블 연관 데이터
  // 존재 시 삭제 순서 / 정책 추가 필요
  // ──────────────────────────────────────────────────────────────
  // @Delete("""
  // DELETE FROM `OUTBOUND`
  // WHERE `id` = #{outboundId}
  // """)
  // public int delete(int outboundId);

  // ──────────────────────────────────────────────────────────────
  // 10. 고객사 목록 조회 (등록 모달 드롭다운)
  // [DB 유지] PARTNER_COMPANY_MASTER.customer_yn_code = 1
  // ──────────────────────────────────────────────────────────────
  @Select("""
      SELECT
        `id`,
        `name`
      FROM `PARTNER_COMPANY_MASTER`
      WHERE `customer_yn_code` = 1
      """)
  public List<OrderCustomerDTO> findByCustomer();

  // ──────────────────────────────────────────────────────────────
  // 11. 완제품 목록 조회 (등록 모달 드롭다운)
  // [DB 유지] OUTBOUND_PRODUCT_MASTER 전체 조회
  // ──────────────────────────────────────────────────────────────
  @Select("""
      SELECT
        `id`,
        `name`
      FROM `OUTBOUND_PRODUCT_MASTER`
      """)
  public List<OrderProductDTO> findByProduct();

}