package cloud.weareithero.api.outbound.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import cloud.weareithero.api.outbound.dto.OutboundCarrierDTO;
import cloud.weareithero.api.outbound.dto.OutboundDTO;
import cloud.weareithero.api.outbound.dto.OutboundInvoiceDTO;
import cloud.weareithero.api.outbound.dto.OutboundManifestDTO;
import cloud.weareithero.api.outbound.dto.OutboundProductDTO;
import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;
import cloud.weareithero.api.outbound.dto.OutboundSummaryDTO;
import cloud.weareithero.api.outbound.dto.OutboundTransportationVehicleDTO;
import cloud.weareithero.api.outbound.dto.OutboundVehicleAssignDTO;

/**
 * Outbound Mapper Interface
 *
 * SQL 구현은 Mapper XML (OutboundMapper.xml)에 작성합니다.
 * Inbound와 달리 @Select/@Insert 어노테이션 인라인 방식을 사용하지 않고
 * XML 방식으로 통일합니다. (동적 쿼리가 많고 UPDATE 구문이 다수 포함되어
 * 가독성을 위해 XML 분리가 적합합니다.)
 *
 * namespace: cloud.weareithero.api.outbound.dao.OutboundMapper
 */
@Mapper
public interface OutboundMapper {

    /**
     * [findSummary]
     * 상단 집계 카드 5종 조회
     * 대상 테이블: OUTBOUND (JOIN ORDER_PRODUCT_LIST, PARTNER_COMPANY_MASTER, COMMON_CODE)
     *
     * 집계 항목:
     *   - total         : 전체 건수
     *   - expectedToday : 출고 예정 (당일 etd 기준, state_code = 출고대기)
     *   - confirmedToday: 출고 확정 (당일 기준, state_code = 출고확정)
     *   - nearDeadline  : 기한 임박 (etd - NOW() < N분, 미확정 건)  ← 기준값 팀 정의 필요
     *   - overdue       : 기한 초과 (etd < NOW(), 미확정 건)
     *   - unassigned    : 미배정 (transportation_vehicle_id IS NULL 또는 state_code = 미배정)
     *
     * ※ 필터 조건(날짜 범위, 주문번호, 고객사명, state_code) 동적 적용 필요
     * ※ "기한 임박" 기준 시간(분) COMMON_CODE 또는 상수로 정의 필요
     */
    public OutboundSummaryDTO findSummary(OutboundRequestDTO outboundRequestDTO);

    /**
     * [findAll]
     * 박스 뷰 목록 조회 (페이지네이션 포함)
     * 대상 테이블: OUTBOUND
     *   JOIN ORDER_PRODUCT_LIST  ON outbound.order_product_list_id = opl.id
     *   JOIN PARTNER_COMPANY_MASTER ON opl.partner_company_id = pcm.id
     *   JOIN OUTBOUND_TRANSPORTATION ON outbound.outbound_transportation_id = ot.id  ← 컬럼명 ERD 확인 필요
     *   JOIN PARTNER_COMPANY_MASTER carrier ON ot.carrier_company_id = carrier.id    ← 운송사
     *   JOIN TRANSPORTATION_VEHICLE_MASTER ON ot.transportation_vehicle_id = tvm.id
     *   JOIN COMMON_CODE ON outbound.state_code = cc.id
     *
     * 반환 컬럼:
     *   outboundId, boxNo(*), orderNo, customerName,
     *   carrierName, stateCode, stateName,
     *   invoiceNo(*), expectedDepartureAt(*), deadline(etd)
     *
     * ※ (*) 표시 컬럼은 ERD에서 컬럼명 및 존재 여부 확인 필요 (아래 ERD 확인 항목 참고)
     * ※ 동적 조건: orderStart/orderEnd, orderNo(LIKE), customerName(LIKE), stateCode
     * ※ 페이지네이션: LIMIT #{offset}, #{size}  (PageRequestDTO.offset 활용)
     */
    public List<OutboundDTO> findAll(OutboundRequestDTO outboundRequestDTO);

    /**
     * [findByOutboundId]
     * 단건 조회 (상세 모달 헤더 및 상태 가드용)
     * 대상 테이블: OUTBOUND (JOIN 동일)
     * WHERE outbound.id = #{outboundId}
     */
    public OutboundDTO findByOutboundId(int outboundId);

    /**
     * [findProducts]
     * 주문 상세 모달 - 제품 목록 조회
     * 대상 테이블: ORDER_PRODUCT
     *   JOIN OUTBOUND_PRODUCT_MASTER ON op.outbound_product_id = opm.id
     *   JOIN PRODUCT_PRICE ON opm.id = pp.outbound_product_id
     * WHERE op.order_product_list_id = #{outboundId}
     *
     * 반환 컬럼:
     *   productName(opm.name), quantity(op.quantity),
     *   totalPrice(op.total_price), boxQty(*), invoiceNo(*)
     *
     * ※ boxQty, invoiceNo 컬럼 ERD 확인 필요
     */
    public List<OutboundProductDTO> findProducts(int outboundId);

    /**
     * [findAllManifest]
     * 매니페스트 뷰 목록 조회 (OUTBOUND_TRANSPORTATION 기준)
     * 대상 테이블: OUTBOUND_TRANSPORTATION
     *   JOIN PARTNER_COMPANY_MASTER carrier ON ot.carrier_company_id = carrier.id
     *   JOIN TRANSPORTATION_VEHICLE_MASTER ON ot.transportation_vehicle_id = tvm.id
     *   JOIN COMMON_CODE ON ot.state_code(*)= cc.id   ← state_code 컬럼 ERD 확인 필요
     *
     * 반환 컬럼:
     *   manifestNo(*), carrierName, vehicleInfo(차량번호 + 종류 조합),
     *   boxCount(COUNT(OUTBOUND) 집계), stateName, expectedDepartureAt(*), deadline
     *
     * ※ manifestNo 채번 방식 및 컬럼 ERD 확인 필요
     * ※ OUTBOUND_TRANSPORTATION에 state_code 컬럼 존재 여부 ERD 확인 필요
     * ※ 동적 조건: orderStart/orderEnd 적용 범위 확인 필요
     * ※ 페이지네이션: LIMIT #{offset}, #{size}
     */
    public List<OutboundManifestDTO> findAllManifest(OutboundRequestDTO outboundRequestDTO);

    /**
     * [findByCarrier]
     * 운송사 목록 조회 (차량 배정 모달 드롭다운용)
     * 대상 테이블: PARTNER_COMPANY_MASTER
     * WHERE carrier_yn_code = 1  ← carrier_yn_code 컬럼 ERD 확인 필요
     *                              (Inbound의 supplier_yn_code = 1 대칭 구조)
     *
     * 반환 컬럼: id, name
     */
    public List<OutboundCarrierDTO> findByCarrier();

    /**
     * [findByVehicle]
     * 차량 목록 조회 (차량 배정 모달 드롭다운용)
     * 대상 테이블: TRANSPORTATION_VEHICLE_MASTER
     *
     * 반환 컬럼: id, vehicleType, vehicleNumber(*)(*), maxPayloadTon
     *
     * ※ vehicleNumber 컬럼명 ERD 확인 필요 (ERD에 vehicle_type, max_payload_ton, max_volume 확인됨)
     */
    public List<OutboundTransportationVehicleDTO> findByVehicle();

    /**
     * [addTransportation]
     * 차량 배정 - OUTBOUND_TRANSPORTATION INSERT
     * @Options(useGeneratedKeys = true, keyProperty = "transportationId") 적용 필수
     *
     * INSERT INTO OUTBOUND_TRANSPORTATION (
     *   transportation_vehicle_id,
     *   carrier_company_id,
     *   bpn(*),          ← 예상 출발 일시 컬럼명 ERD 확인 필요
     *   driver,
     *   ...              ← 기사 연락처 컬럼 ERD 확인 필요
     * ) VALUES (...)
     *
     * ※ OutboundVehicleAssignDTO.transportationId 에 PK 자동 주입됨
     */
    @Options(useGeneratedKeys = true, keyProperty = "transportationId")
    public int addTransportation(OutboundVehicleAssignDTO outboundVehicleAssignDTO);

    /**
     * [updateTransportationId]
     * 차량 배정 - 개별 OUTBOUND에 transportation_id 연결 + state_code 변경
     *
     * UPDATE OUTBOUND
     * SET outbound_transportation_id(*) = #{transportationId},
     *     state_code = [차량배정완료 코드]    ← COMMON_CODE 확인 필요
     * WHERE id = #{outboundId}
     *
     * ※ OUTBOUND 테이블에 transportation 연결 FK 컬럼명 ERD 확인 필요
     * @Param 어노테이션으로 두 파라미터 바인딩
     */
    public int updateTransportationId(@Param("outboundId") int outboundId,
                                      @Param("transportationId") int transportationId);

    /**
     * [updateInvoice]
     * 송장 발급 - OUTBOUND UPDATE
     *
     * UPDATE OUTBOUND
     * SET invoice_no(*) = #{invoiceNo},   ← invoice_no 컬럼 ERD 확인 필요
     *     state_code    = [송장발급완료 코드]  ← COMMON_CODE 확인 필요
     * WHERE id = #{outboundId}
     *
     * ※ invoiceNo 채번 로직:
     *   - 옵션 A) SQL 내 DATE_FORMAT + LPAD(COUNT+1) 방식 (동시성 취약)
     *   - 옵션 B) ServiceImp에서 채번 후 DTO에 담아 전달 (권장)
     *   → 팀 결정 필요
     */
    public int updateInvoice(OutboundInvoiceDTO outboundInvoiceDTO);

    /**
     * [updateStateCode]
     * 출고 확정 - state_code 단순 UPDATE
     *
     * UPDATE OUTBOUND
     * SET state_code = #{stateCode}
     * WHERE id = #{outboundId}
     *
     * @Param 어노테이션으로 두 파라미터 바인딩
     */
    public int updateStateCode(@Param("outboundId") int outboundId,
                               @Param("stateCode") int stateCode);

}