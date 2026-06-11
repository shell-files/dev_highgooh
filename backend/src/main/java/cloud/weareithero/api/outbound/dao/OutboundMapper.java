package cloud.weareithero.api.outbound.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import cloud.weareithero.api.outbound.dto.OutboundCarrierDTO;
import cloud.weareithero.api.outbound.dto.OutboundDTO;
import cloud.weareithero.api.outbound.dto.OutboundInvoiceDTO;
import cloud.weareithero.api.outbound.dto.OutboundManifestDTO;
import cloud.weareithero.api.outbound.dto.OutboundPackingDTO;
import cloud.weareithero.api.outbound.dto.OutboundProductDTO;
import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;
import cloud.weareithero.api.outbound.dto.OutboundSummaryDTO;
import cloud.weareithero.api.outbound.dto.OutboundTransportationVehicleDTO;
import cloud.weareithero.api.outbound.dto.OutboundVehicleAssignDTO;

/**
 * OutboundMapper
 * SQL은 OutboundMapper.xml 에 작성합니다.
 * namespace: cloud.weareithero.api.outbound.dao.OutboundMapper
 *
 * ──────────────────────────────────────────────────────────────────
 * [최신 DB 3차안 기준 OUTBOUND 테이블 구조 요약]
 *
 * OUTBOUND
 *   id, partner_company_id, order_date, deadline, etd, state_code, updated_at
 *   ※ OUTBOUND가 직접 partner_company_id(고객사) 보유
 *   ※ 이전 설계의 ORDER_PRODUCT_LIST 테이블 없음
 *
 * ORDER_PRODUCT
 *   id, outbound_id, outbound_product_id, quantity, price, total_price
 *   ※ outbound_id → OUTBOUND.id 직접 연결
 *
 * OUTBOUND_PACKING (박스 단위)
 *   id, outbound_id, packing_invoice_number, outbound_product_id,
 *   partner_company_id(운송사FK), invoice_number, outbound_transportation_id,
 *   state_code, updated_at
 *   ※ 박스 뷰의 1행 = OUTBOUND_PACKING 1건
 *   ※ packing_invoice_number: 패킹 송장번호 (박스 식별용)
 *   ※ invoice_number: 운송장번호 (발급 후 저장)
 *   ※ partner_company_id: 운송사 FK (carrier_yn_code=1)
 *   ※ outbound_transportation_id: 차량 배정 후 연결
 *   ※ "백엔드 작업 중 수정 필요 시 요청 바람" 주석 있음 → 구조 변경 가능성
 *
 * OUTBOUND_TRANSPORTATION (매니페스트 단위)
 *   id, carrier_company_id, transportation_vehicle_id, lpn, driver,
 *   etd, atd, state_code, updated_at
 *   ※ 매니페스트 뷰의 1행 = OUTBOUND_TRANSPORTATION 1건
 *   ※ etd: 예상 출고 일자 (차량 배정 시 입력)
 *   ※ atd: 실제 출고 일자 (출고 확정 시 NOW())
 *
 * PARTNER_COMPANY_MASTER
 *   id, name, supplier_yn_code, customer_yn_code, carrier_yn_code
 *   ※ 고객사: customer_yn_code=1, 운송사: carrier_yn_code=1
 * ──────────────────────────────────────────────────────────────────
 */
@Mapper
public interface OutboundMapper {

    /**
     * [selectOutboundSummary] → findSummary
     *
     * OUTBOUND 기준 상단 집계 카드 5종
     *
     * 집계 항목:
     *   total         - 전체 OUTBOUND 건수
     *   expectedToday - 당일 etd 기준 미완료 건 (OUTBOUND.etd = CURDATE())
     *   confirmedToday- 당일 atd 기준 완료 건 (OUTBOUND_TRANSPORTATION.atd = CURDATE())
     *                   ※ OUTBOUND 직접 atd 없음 → OUTBOUND_PACKING → OUTBOUND_TRANSPORTATION 경로
     *                   ※ atd는 OUTBOUND_TRANSPORTATION에만 존재
     *   nearDeadline  - 기한 임박: OUTBOUND.deadline 기준 DATEDIFF 3일 이내 미완료
     *                   ※ 기준일수(3일) 팀 정의 후 확정
     *   overdue       - 기한 초과: OUTBOUND.deadline < CURDATE(), 미완료 건
     *   unassigned    - 미배정: OUTBOUND_PACKING.outbound_transportation_id IS NULL
     *
     * 동적 조건: orderStart/orderEnd(OUTBOUND.order_date), outboundId(OUTBOUND.id LIKE),
     *           customerName(PARTNER_COMPANY_MASTER.name LIKE), stateCode(OUTBOUND.state_code)
     */
    public OutboundSummaryDTO findSummary(OutboundRequestDTO outboundRequestDTO);

    /**
     * [selectOutboundList] → findAll
     *
     * OUTBOUND_PACKING 박스 목록 (페이지네이션 포함)
     *
     * JOIN 경로:
     *   OUTBOUND_PACKING op
     *     → OUTBOUND ob               ON op.outbound_id = ob.id
     *       → PARTNER_COMPANY_MASTER pcm  ON ob.partner_company_id = pcm.id (고객사)
     *     → PARTNER_COMPANY_MASTER carrier ON op.partner_company_id = carrier.id (운송사, LEFT JOIN)
     *       ※ op.partner_company_id = 운송사 FK (DB 설계 확인됨)
     *     → OUTBOUND_TRANSPORTATION ot  ON op.outbound_transportation_id = ot.id (LEFT JOIN)
     *     → COMMON_CODE cc              ON op.state_code = cc.id
     *
     * 반환 컬럼:
     *   packingId(op.id), packingInvoiceNumber(packing_invoice_number),
     *   outboundId(ob.id), customerName(pcm.name), carrierName(carrier.name),
     *   invoiceNumber(op.invoice_number), stateCode(op.state_code), stateName(cc.name),
     *   deadline(ob.deadline), etd(ob.etd), transportationId(op.outbound_transportation_id)
     *
     * ※ 동적 조건: outboundId(ob.id LIKE), customerName(pcm.name LIKE),
     *              orderStart/orderEnd(ob.order_date), stateCode(op.state_code)
     * ※ ORDER BY op.id DESC, LIMIT #{offset}, #{size}
     *
     * [화면-DB 불일치 주의]
     * - JSX의 boxNo 필드 = op.packing_invoice_number 로 매핑
     * - JSX의 orderNo 필드 = ob.id (OUTBOUND.id) 로 매핑
     *   ('PO-YYYYMMDD-' 형식 표현이 필요하면 CONCAT 가공 또는 프론트 처리)
     * - JSX의 due 필드 = ob.deadline 기준으로 프론트가 계산 (백엔드는 날짜 반환)
     */
    public List<OutboundPackingDTO> findAll(OutboundRequestDTO outboundRequestDTO);

    /**
     * [selectOutboundDetail] → findByOutboundId
     *
     * OUTBOUND 단건 조회 (상세 모달 헤더용)
     *
     * JOIN 경로:
     *   OUTBOUND ob
     *     → PARTNER_COMPANY_MASTER pcm ON ob.partner_company_id = pcm.id
     *     → COMMON_CODE cc             ON ob.state_code = cc.id
     *
     * WHERE ob.id = #{outboundId}
     */
    public OutboundDTO findByOutboundId(int outboundId);

    /**
     * [selectPackingDetail] → findByPackingId
     *
     * OUTBOUND_PACKING 단건 조회 (issueInvoice 상태 가드용)
     * WHERE id = #{packingId}
     */
    public OutboundPackingDTO findByPackingId(int packingId);

    /**
     * [selectOrderProducts] → findProducts
     *
     * ORDER_PRODUCT 목록 조회 (상세 모달 제품 목록)
     *
     * JOIN 경로:
     *   ORDER_PRODUCT op
     *     → OUTBOUND_PRODUCT_MASTER opm ON op.outbound_product_id = opm.id
     *
     * WHERE op.outbound_id = #{outboundId}
     *
     * 반환 컬럼:
     *   productId(op.id), productName(opm.name),
     *   quantity(op.quantity), price(op.price), totalPrice(op.total_price)
     *
     * ※ DB 설계상 ORDER_PRODUCT에 box_id 없음 → boxQty 산출 불가
     *   OUTBOUND_PACKING.outbound_product_id 로 연결은 가능하나 COUNT 집계 필요
     *   현재 구현에서는 제외, 필요 시 서브쿼리 추가
     * ※ invoice_number: ORDER_PRODUCT 레벨이 아닌 OUTBOUND_PACKING 레벨에 존재
     *   상세 제품별 송장번호 표시 필요 시 OUTBOUND_PACKING 별도 조회 필요
     */
    public List<OutboundProductDTO> findProducts(int outboundId);

    /**
     * [selectManifestList] → findAllManifest
     *
     * OUTBOUND_TRANSPORTATION 기준 매니페스트 목록 (페이지네이션 포함)
     *
     * JOIN 경로:
     *   OUTBOUND_TRANSPORTATION ot
     *     → PARTNER_COMPANY_MASTER carrier ON ot.carrier_company_id = carrier.id
     *     → TRANSPORTATION_VEHICLE_MASTER  tvm ON ot.transportation_vehicle_id = tvm.id
     *     → OUTBOUND_PACKING op             ON op.outbound_transportation_id = ot.id (LEFT JOIN)
     *       → OUTBOUND ob                  ON op.outbound_id = ob.id (LEFT JOIN)
     *         → PARTNER_COMPANY_MASTER pcm ON ob.partner_company_id = pcm.id (필터용, LEFT JOIN)
     *     → COMMON_CODE cc                  ON ot.state_code = cc.id
     *
     * GROUP BY: ot.id (OUTBOUND_PACKING COUNT 집계)
     *
     * 반환 컬럼:
     *   transportationId(ot.id), carrierName(carrier.name),
     *   vehicleInfo(CONCAT(ot.lpn, ' (', tvm.vehicle_type, ')')),
     *   boxCount(COUNT(op.id)), stateCode(ot.state_code), stateName(cc.name),
     *   etd(ot.etd), atd(ot.atd), driver(ot.driver)
     *
     * ※ manifestNo: OUTBOUND_TRANSPORTATION.id 를 LPAD 포맷으로 가공
     *   (예: CONCAT('MNF-', LPAD(ot.id, 6, '0')) → 별도 컬럼 없음)
     * ※ 동적 조건: stateCode(ot.state_code), customerName(pcm.name LIKE, HAVING 또는 WHERE)
     *              orderStart/orderEnd(ob.order_date 기준, LEFT JOIN 후 WHERE)
     * ※ ORDER BY ot.id DESC, LIMIT #{offset}, #{size}
     *
     * [화면-DB 불일치 주의]
     * - JSX의 due 필드 = ot.etd 기준으로 프론트가 계산
     * - JSX 체크박스 활성 조건: status === '차량배정'
     *   → COMMON_CODE에서 '차량배정' state_code 값 확인 필요
     */
    public List<OutboundManifestDTO> findAllManifest(OutboundRequestDTO outboundRequestDTO);

    /**
     * [selectCarrierList] → findByCarrier
     *
     * 운송사 목록 조회 (차량 배정 모달 드롭다운)
     * SELECT id, name FROM PARTNER_COMPANY_MASTER WHERE carrier_yn_code = 1
     */
    public List<OutboundCarrierDTO> findByCarrier();

    /**
     * [selectVehicleList] → findByVehicle
     *
     * 차량 목록 조회 (차량 배정 모달 드롭다운)
     * SELECT id, vehicle_type, max_payload_ton FROM TRANSPORTATION_VEHICLE_MASTER
     */
    public List<OutboundTransportationVehicleDTO> findByVehicle();

    /**
     * [insertTransportation] → addTransportation
     *
     * OUTBOUND_TRANSPORTATION INSERT
     *
     * 저장 컬럼:
     *   carrier_company_id (carrierId)
     *   transportation_vehicle_id (vehicleId)
     *   lpn (차량번호 직접 입력)
     *   driver (기사명)
     *   etd (예상 출고 일자)
     *   state_code = 차량배정 상태 ← COMMON_CODE 확인 후 값 결정 필요
     *
     * @Options(useGeneratedKeys = true, keyProperty = "transportationId")
     * → 생성된 PK를 OutboundVehicleAssignDTO.transportationId 에 자동 주입
     */
    @Options(useGeneratedKeys = true, keyProperty = "transportationId")
    public int addTransportation(OutboundVehicleAssignDTO outboundVehicleAssignDTO);

    /**
     * [updatePackingTransportation] → updatePackingTransportation
     *
     * 차량 배정 - OUTBOUND_PACKING UPDATE
     *
     * UPDATE OUTBOUND_PACKING
     * SET outbound_transportation_id = #{transportationId},
     *     state_code = [차량배정 완료 코드],   ← COMMON_CODE 확인 필요
     *     updated_at = NOW()
     * WHERE id = #{packingId}
     *
     * @Param 두 파라미터 바인딩
     */
    public int updatePackingTransportation(@Param("packingId") int packingId,
                                           @Param("transportationId") int transportationId);

    /**
     * [updateInvoiceNumber] → updateInvoiceNumber
     *
     * 송장 발급 - OUTBOUND_PACKING UPDATE
     *
     * UPDATE OUTBOUND_PACKING
     * SET invoice_number = #{invoiceNumber},
     *     state_code = [송장발급완료 코드],    ← COMMON_CODE 확인 필요
     *     updated_at = NOW()
     * WHERE id = #{packingId}
     *
     * ※ invoiceNumber는 ServiceImp에서 채번 후 DTO에 담아 전달
     * ※ packing_invoice_number 와 invoice_number 는 별개
     *   - packing_invoice_number: 패킹 시 생성 (박스 식별용)
     *   - invoice_number: 송장 발급 후 저장 (운송장번호)
     */
    public int updateInvoiceNumber(OutboundInvoiceDTO outboundInvoiceDTO);

    /**
     * [updateShipmentConfirm] → updateTransportationConfirm
     *
     * 출고 확정 - OUTBOUND_TRANSPORTATION UPDATE
     *
     * UPDATE OUTBOUND_TRANSPORTATION
     * SET state_code = #{stateCode},
     *     atd = NOW(),
     *     updated_at = NOW()
     * WHERE id = #{transportationId}
     *
     * @Param 두 파라미터 바인딩
     */
    public int updateTransportationConfirm(@Param("transportationId") int transportationId,
                                           @Param("stateCode") int stateCode);

}