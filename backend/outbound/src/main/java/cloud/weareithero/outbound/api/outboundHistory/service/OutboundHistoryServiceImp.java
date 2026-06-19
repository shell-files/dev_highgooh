package cloud.weareithero.outbound.api.outboundHistory.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cloud.weareithero.outbound.api.outboundHistory.dao.OutboundHistoryDao;
import cloud.weareithero.outbound.api.outboundHistory.dto.OutboundHistoryDashboardStatsDTO;
import cloud.weareithero.outbound.api.outboundHistory.dto.OutboundHistoryRequestDTO;
import cloud.weareithero.outbound.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboundHistoryServiceImp implements OutboundHistoryService {

  private final OutboundHistoryDao outboundHistoryDao;

  @Override
  public ResponseDTO getDashboardStats(OutboundHistoryRequestDTO requestDTO) {
    boolean isSuccess = false;
    String message;
    OutboundHistoryDashboardStatsDTO stats = null;

    try {
        // 1. 통계 데이터 조회 (상단 카드용 카운트)
        stats = outboundHistoryDao.getDashboardStats(requestDTO);
        
        if (stats != null) {
            // 2. 하단 테이블 리스트 데이터 조회 (DAO를 통해 안전하게 호출)
            List<Map<String, Object>> list = outboundHistoryDao.getHistoryList(requestDTO);
            
            list.forEach(row -> {
                Object updatedAt = row.get("updated_at");
                if (updatedAt != null) {
                    row.put("updated_at", updatedAt.toString().substring(0, 10));
                }
            });

            long totalCount = outboundHistoryDao.getHistoryListCount(requestDTO);
            
            // 3. 조회한 데이터를 stats 객체에 매핑
            stats.setList(list);
            stats.setTotalCount(totalCount);
            
            // 4. 전체 페이지 수 계산 (0 나누기 방지 처리)
            int pageSize = (requestDTO.getSize() > 0) ? requestDTO.getSize() : 20;
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);
            stats.setTotalPages(totalPages);
        }
        
        isSuccess = true;
        message = "출고 패킹 대시보드 통계 및 목록 조회가 완료되었습니다.";
    } catch (Exception e) {
        log.error("OutboundHistoryServiceImp getDashboardStats error : {}", e.getMessage(), e);
        message = "출고 대시보드 조회 중 오류가 발생했습니다: " + e.getMessage();
    }

    return ResponseDTO.builder()
        .status(isSuccess)
        .data(stats)
        .message(message)
        .build();
  }

  @Override
  public ResponseDTO getOutboundDetail(Integer outboundId) {
    try {
      // 💡 Mapper를 직접 호출하지 않고 DAO를 통해 상세 내역을 조회합니다.
      List<Map<String, Object>> detailList = outboundHistoryDao.getOutboundDetailList(outboundId);
      
      return ResponseDTO.builder()
          .status(true)
          .message("출고 상세 내역 조회가 정상적으로 완료되었습니다.")
          .data(detailList)
          .build();

    } catch (Exception e) {
      log.error("OutboundHistoryServiceImp getOutboundDetail 조회 오류 : {}", e.getMessage(), e);
      return ResponseDTO.builder()
          .status(false)
          .message("상세 내역 조회 중 장애 발생: " + e.getMessage())
          .build();
    }
  }
  
  @Override
  public ResponseDTO getPartnerCompanies() {
    try {
      // Mapper를 직접 바라보지 않고 DAO를 거쳐서 호출
      List<Map<String, Object>> partners = outboundHistoryDao.getPartnerCompanies();
      
      return ResponseDTO.builder()
          .status(true)
          .message("파트너사 목록 조회가 완료되었습니다.")
          .data(partners)
          .build();
    } catch (Exception e) {
      log.error("OutboundHistoryServiceImp getPartnerCompanies error : {}", e.getMessage(), e);
      return ResponseDTO.builder()
          .status(false)
          .message("파트너사 목록 조회 중 오류가 발생했습니다: " + e.getMessage())
          .build();
    }
  }
}