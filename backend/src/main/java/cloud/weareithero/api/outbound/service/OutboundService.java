package cloud.weareithero.api.outbound.service;

import cloud.weareithero.api.outbound.dto.OutboundConfirmDTO;
import cloud.weareithero.api.outbound.dto.OutboundInvoiceDTO;
import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;
import cloud.weareithero.api.outbound.dto.OutboundVehicleAssignDTO;
import cloud.weareithero.dto.ResponseDTO;

public interface OutboundService {

    /** 박스 뷰 목록 조회 (OUTBOUND_PACKING 기준) */
    public ResponseDTO findAll(OutboundRequestDTO outboundRequestDTO);

    /** 주문 상세 조회 (OUTBOUND 헤더 + ORDER_PRODUCT 목록) */
    public ResponseDTO findOne(int outboundId);

    /** 매니페스트 뷰 목록 조회 (OUTBOUND_TRANSPORTATION 기준) */
    public ResponseDTO findAllManifest(OutboundRequestDTO outboundRequestDTO);

    /** 폼 데이터 조회 (운송사·차량 목록) */
    public ResponseDTO findAllOutbound();

    /** 차량 배정 */
    public ResponseDTO assignVehicle(OutboundVehicleAssignDTO outboundVehicleAssignDTO);

    /** 송장 발급 */
    public ResponseDTO issueInvoice(OutboundInvoiceDTO outboundInvoiceDTO);

    /** 출고 확정 */
    public ResponseDTO confirmShipment(OutboundConfirmDTO outboundConfirmDTO);

    /** 매니페스트에 속한 박스 목록 조회 */
    public ResponseDTO findPackingsByTransportationId(int transportationId);

    // 송장처리
    public ResponseDTO issueInvoiceByTransportation(int transportationId);
}