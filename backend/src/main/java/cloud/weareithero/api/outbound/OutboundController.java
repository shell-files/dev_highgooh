package cloud.weareithero.api.outbound;

import org.springframework.web.bind.annotation.GetMapping;
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
     */
    @PostMapping
    public ResponseDTO findAll(@RequestBody OutboundRequestDTO outboundRequestDTO) {
        return outboundService.findAll(outboundRequestDTO);
    }

    /**
     * 주문 상세 내역 조회 (제품 목록 포함)
     * POST /outbound/{outboundId}
     */
    @PostMapping("/{outboundId:[0-9]+}")
    public ResponseDTO findOne(@PathVariable Integer outboundId) {
        return outboundService.findOne(outboundId);
    }

    /**
     * 매니페스트 뷰 목록 조회
     * POST /outbound/manifest
     */
    @PostMapping("/manifest")
    public ResponseDTO findAllManifest(@RequestBody OutboundRequestDTO outboundRequestDTO) {
        return outboundService.findAllManifest(outboundRequestDTO);
    }

    /**
     * 폼 데이터 조회 (운송사·차량 목록)
     * GET /outbound
     */
    @GetMapping
    public ResponseDTO findAllOutbound() {
        return outboundService.findAllOutbound();
    }

    /**
     * 차량 배정
     * PUT /outbound/vehicle
     */
    @PutMapping("/vehicle")
    public ResponseDTO assignVehicle(@RequestBody OutboundVehicleAssignDTO outboundVehicleAssignDTO) {
        return outboundService.assignVehicle(outboundVehicleAssignDTO);
    }

    /**
     * 송장 발급
     * PUT /outbound/invoice
     */
    @PutMapping("/invoice")
    public ResponseDTO issueInvoice(@RequestBody OutboundInvoiceDTO outboundInvoiceDTO) {
        return outboundService.issueInvoice(outboundInvoiceDTO);
    }

    /**
     * 출고 확정
     * PUT /outbound/confirm
     */
    @PutMapping("/confirm")
    public ResponseDTO confirmShipment(@RequestBody OutboundConfirmDTO outboundConfirmDTO) {
        return outboundService.confirmShipment(outboundConfirmDTO);
    }

}