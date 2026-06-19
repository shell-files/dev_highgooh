package cloud.weareithero.outbound.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderSummaryDTO - 화면 상단 요약 카드 4개
 * total / newOrder / inProgress / completed
 * Mapper findSummary SQL의 AS 별칭과 1:1 일치 필수
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummaryDTO {

  private int total;       // 전체
  private int newOrder;    // 신규
  private int inProgress;  // 처리중
  private int completed;   // 풀고완료

}