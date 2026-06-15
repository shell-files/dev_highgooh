package cloud.weareithero.api.outbound;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.api.outbound.dto.OutboundConfirmDTO;
import cloud.weareithero.api.outbound.dto.OutboundInvoiceDTO;
import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;
import cloud.weareithero.api.outbound.dto.OutboundVehicleAssignDTO;
import cloud.weareithero.api.outbound.service.OutboundService;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/outbound")
@RequiredArgsConstructor
public class OutboundController implements OutboundControllerDocs {

    private final OutboundService outboundService;

    /**
     * 박스 뷰 목록 조회
     * POST /outbound
     * - OUTBOUND_PACKING 기준 목록
     * - 필터: 주문번호(outboundId), 고객사명, 상태, 날짜 범위
     */
    @PostMapping
    public ResponseDTO findAll(@RequestBody OutboundRequestDTO outboundRequestDTO) {
        return outboundService.findAll(outboundRequestDTO);
    }

    /**
     * 주문 상세 조회 (OUTBOUND 단건 + ORDER_PRODUCT 목록)
     * POST /outbound/{outboundId}
     */
    @PostMapping("/{outboundId:[0-9]+}")
    public ResponseDTO findOne(@PathVariable Integer outboundId) {
        return outboundService.findOne(outboundId);
    }

    /**
     * 매니페스트 뷰 목록 조회
     * POST /outbound/manifest
     * - OUTBOUND_TRANSPORTATION 기준 목록
     */
    @PostMapping("/manifest")
    public ResponseDTO findAllManifest(@RequestBody OutboundRequestDTO outboundRequestDTO) {
        return outboundService.findAllManifest(outboundRequestDTO);
    }

    /**
     * 폼 데이터 조회 (차량 배정 모달 드롭다운용)
     * GET /outbound
     * - 운송사 목록 (carrier_yn_code=1), 차량 목록
     */
    @GetMapping
    public ResponseDTO findAllOutbound() {
        return outboundService.findAllOutbound();
    }

    /**
     * 차량 배정
     * PUT /outbound/vehicle
     * - OUTBOUND_TRANSPORTATION INSERT → OUTBOUND_PACKING.outbound_transportation_id UPDATE
     */
    @PutMapping("/vehicle")
    public ResponseDTO assignVehicle(@RequestBody OutboundVehicleAssignDTO outboundVehicleAssignDTO) {
        return outboundService.assignVehicle(outboundVehicleAssignDTO);
    }

    /**
     * 송장 발급 (운송장번호 저장)
     * PUT /outbound/invoice
     * - OUTBOUND_PACKING.invoice_number UPDATE
     * - OUTBOUND_PACKING.state_code 변경
     */
    @PutMapping("/invoice")
    public ResponseDTO issueInvoice(@RequestBody OutboundInvoiceDTO outboundInvoiceDTO) {
        return outboundService.issueInvoice(outboundInvoiceDTO);
    }

    /**
     * 출고 확정
     * PUT /outbound/confirm
     * - OUTBOUND_TRANSPORTATION.state_code UPDATE (출고완료)
     * - OUTBOUND_TRANSPORTATION.atd = NOW()
     * ※ 매니페스트 탭에서 매니페스트 단위로 확정 처리
     */
    @PutMapping("/confirm")
    public ResponseDTO confirmShipment(@RequestBody OutboundConfirmDTO outboundConfirmDTO) {
        return outboundService.confirmShipment(outboundConfirmDTO);
    }
    @GetMapping("/manifest/{transportationId}/packings")
    public ResponseDTO findPackingsByTransportationId(@PathVariable Integer transportationId) {
        return outboundService.findPackingsByTransportationId(transportationId);
    }

    @PatchMapping("/manifest/{transportationId}/issue")
    public ResponseDTO issueInvoiceByTransportation(@PathVariable Integer transportationId) {
        return outboundService.issueInvoiceByTransportation(transportationId);
    }
}