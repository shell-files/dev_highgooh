package cloud.weareithero.api.outbound.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 출고 화면 상단 집계 카드 5종
 * Inbound의 AsnSummaryDTO(3종)와 달리 5종 집계
 *
 * 집계 기준: OUTBOUND 테이블
 * ─────────────────────────────────────────
 * total         - 전체 OUTBOUND 건수 (페이지네이션 totalCount 용도)
 * expectedToday - 당일 출고 예정 (OUTBOUND.etd = CURDATE(), 미완료)
 * confirmedToday- 당일 출고 확정 (OUTBOUND_TRANSPORTATION.atd = CURDATE())
 *                 ※ OUTBOUND에 atd 없음 → OUTBOUND_PACKING → OT 경로 집계 필요
 * nearDeadline  - 기한 임박 (OUTBOUND.deadline - CURDATE() ≤ 3일, 미완료)
 *                 ※ 기준일수 팀 협의 필요
 * overdue       - 기한 초과 (OUTBOUND.deadline < CURDATE(), 미완료)
 * unassigned    - 미배정 (OUTBOUND_PACKING.outbound_transportation_id IS NULL)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboundSummaryDTO {

    private int total;
    private int expectedToday;
    private int confirmedToday;
    private int nearDeadline;
    private int overdue;
    private int unassigned;

}