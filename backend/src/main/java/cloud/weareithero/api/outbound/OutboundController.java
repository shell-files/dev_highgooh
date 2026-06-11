package cloud.weareithero.api.outbound;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;
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

}