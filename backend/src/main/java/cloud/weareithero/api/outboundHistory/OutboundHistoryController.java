package cloud.weareithero.api.outboundHistory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // 추가
import org.springframework.web.bind.annotation.PostMapping; // 추가
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.api.outboundHistory.dto.OutboundHistoryRequestDTO;
import cloud.weareithero.api.outboundHistory.service.OutboundHistoryService;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/outboundHistory")
@RequiredArgsConstructor
public class OutboundHistoryController implements OutboundHistoryControllerDocs {

  private final OutboundHistoryService outboundHistoryService;

  // 1. 기존 대시보드 통계 조회 (유지)
  @PostMapping
  @Override
  public ResponseDTO getDashboardStats(@RequestBody OutboundHistoryRequestDTO requestDTO) {
    return outboundHistoryService.getDashboardStats(requestDTO);
  }

  // 2. [추가] 상세 내역 조회를 위한 엔드포인트
  // 경로: GET /outboundHistory/detail/{outboundId}
  @PostMapping("/{outboundId}")
  public ResponseDTO getOutboundDetail(@PathVariable Integer outboundId) {
    return outboundHistoryService.getOutboundDetail(outboundId);
  }
  @GetMapping("/partners")
    public ResponseDTO getPartners() {
    // Service를 거쳐 mapper.getPartnerCompanies() 호출 결과 반환
    return outboundHistoryService.getPartnerCompanies(); 
}
}