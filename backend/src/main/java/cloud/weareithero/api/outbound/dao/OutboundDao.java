package cloud.weareithero.api.outbound.dao;

import java.util.List;

import cloud.weareithero.api.outbound.dto.OutboundCarrierDTO;
import cloud.weareithero.api.outbound.dto.OutboundClientDTO;
import cloud.weareithero.api.outbound.dto.OutboundDTO;
import cloud.weareithero.api.outbound.dto.OutboundManifestDTO;
import cloud.weareithero.api.outbound.dto.OutboundPackingDTO;
import cloud.weareithero.api.outbound.dto.OutboundProductDTO;
import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;
import cloud.weareithero.api.outbound.dto.OutboundSummaryDTO;
import cloud.weareithero.api.outbound.dto.OutboundTransportationVehicleDTO;
import cloud.weareithero.api.outbound.dto.OutboundVehicleAssignDTO;

public interface OutboundDao {

    /** OUTBOUND 기준 집계 5종 */
    public OutboundSummaryDTO findSummary(OutboundRequestDTO outboundRequestDTO);

    /** OUTBOUND_PACKING 박스 목록 (필터 + 페이지네이션) */
    public List<OutboundPackingDTO> findAll(OutboundRequestDTO outboundRequestDTO);

    /** OUTBOUND 단건 조회 */
    public OutboundDTO findByOutboundId(int outboundId);

    /** OUTBOUND_PACKING 단건 조회 (상태 가드용) */
    public OutboundPackingDTO findByPackingId(int packingId);

    /** ORDER_PRODUCT 목록 조회 */
    public List<OutboundProductDTO> findProducts(int outboundId);

    /** OUTBOUND_TRANSPORTATION 기준 매니페스트 목록 */
    public List<OutboundManifestDTO> findAllManifest(OutboundRequestDTO outboundRequestDTO);

    /** 운송사 목록 (carrier_yn_code = 1) */
    public List<OutboundCarrierDTO> findByCarrier();

    /** 차량 목록 */
    public List<OutboundTransportationVehicleDTO> findByVehicle();

    /** OUTBOUND_TRANSPORTATION INSERT - useGeneratedKeys로 transportationId 반환 */
    public int addTransportation(OutboundVehicleAssignDTO outboundVehicleAssignDTO);

    /** OUTBOUND_PACKING에 transportationId 연결 UPDATE */
    public int updatePackingTransportation(int packingId, int transportationId, int shippingReadyCode);

    /** OUTBOUND_PACKING invoice_number + state_code UPDATE */
    public int updateInvoiceNumber(int packingId, String invoiceNumber, int invoiceStateCode);

    /** OUTBOUND_TRANSPORTATION 출고확정 UPDATE (state_code + atd) */
    public int updateTransportationConfirm(int transportationId, int stateCode);

    /** 매니페스트에 속한 박스 목록 (transportationId 기준) */
    public List<OutboundPackingDTO> findPackingsByTransportationId(int transportationId);

    // 클라이언트 조회
    public List<OutboundClientDTO> findByClient();

    // 페이지네이션 토탈페이지
    public int countAll(OutboundRequestDTO outboundRequestDTO);

    public int updatePackingStateByTransportationId(int transportationId, int stateCode);

    public int updateTransportationStateTo22(int transportationId);
}