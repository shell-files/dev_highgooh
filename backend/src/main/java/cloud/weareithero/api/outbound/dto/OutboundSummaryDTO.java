package cloud.weareithero.api.outbound.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 출고 화면 상단 집계 카드 5종
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboundSummaryDTO {

    private int total;           // 전체 건수 (페이지네이션 totalCount 용도로도 사용)
    private int expectedToday;   // 출고 예정 (당일)
    private int confirmedToday;  // 출고 확정 (당일)
    private int nearDeadline;    // 기한 임박 (기준: 팀 정의 필요 — etd - NOW() < N분)
    private int overdue;         // 기한 초과 (etd < NOW(), 미확정 건)
    private int unassigned;      // 미배정 (차량 미배정 건)

}