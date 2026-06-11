package cloud.weareithero.api.outbound.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OUTBOUND 단건 조회 DTO (상세 모달 헤더용)
 *
 * 기준 테이블: OUTBOUND
 *   id, partner_company_id, order_date, deadline, etd, state_code, updated_at
 *
 * JOIN:
 *   PARTNER_COMPANY_MASTER (고객사명)
 *   COMMON_CODE (상태명)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutboundDTO {

    private int outboundId;         // OUTBOUND.id
    private int partnerId;          // OUTBOUND.partner_company_id
    private String partnerName;     // PARTNER_COMPANY_MASTER.name (고객사)
    private LocalDate orderDate;    // OUTBOUND.order_date
    private LocalDate deadline;     // OUTBOUND.deadline (주문 마감일)
    private LocalDate etd;          // OUTBOUND.etd (출발 예정일)
    private int stateCode;          // OUTBOUND.state_code
    private String stateName;       // COMMON_CODE.name

}