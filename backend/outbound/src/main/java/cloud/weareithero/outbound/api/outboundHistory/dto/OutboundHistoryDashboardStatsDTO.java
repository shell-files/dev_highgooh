package cloud.weareithero.outbound.api.outboundHistory.dto;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "출고 대시보드 패킹 통계 및 목록 데이터")
public class OutboundHistoryDashboardStatsDTO {

  @Schema(description = "전체 완료 건수", example = "15")
  private long totalCompletedCount;

  @Schema(description = "기간내 완료 건수", example = "11")
  private long onTimeCompletedCount;

  @Schema(description = "지연 완료 건수", example = "4")
  private long delayedCompletedCount;

  @Schema(description = "필터링 및 페이지네이션 처리된 테이블 목록")
  private List<Map<String, Object>> list;

  @Schema(description = "테이블 총 레코드 수 (페이지네이션 계산용)", example = "120")
  private long totalCount;

  @Schema(description = "전체 페이지 수", example = "6")
  private int totalPages;
}