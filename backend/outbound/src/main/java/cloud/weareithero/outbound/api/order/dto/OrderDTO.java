package cloud.weareithero.outbound.api.order.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderDTO - 주문 마스터 (OUTBOUND 테이블 매핑)
 *
 * [DB] OUTBOUND 컬럼 기준 재정의:
 *   id                → outboundId
 *   partner_company_id → partnerCompanyId
 *   order_date        → orderDate
 *   deadline          → deadline
 *   state_code        → COMMON_CODE.name AS stateCode (문자열)
 *   totalPrice        → ORDER_PRODUCT SUM 서브쿼리 집계 (목록 표시용)

 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

  private int outboundId;          // OUTBOUND.id (PK)
  private int partnerCompanyId;    // OUTBOUND.partner_company_id
  private String partnerName;      // PARTNER_COMPANY_MASTER.name (JOIN)
  private LocalDate orderDate;     // OUTBOUND.order_date
  private LocalDate deadline;      // OUTBOUND.deadline
  private LocalDate etd;
  private Long totalPrice;         // ORDER_PRODUCT.total_price SUM (목록 집계용)
  private Integer totalQuantity;
  private int stepCode;
  private String stateCode;        // COMMON_CODE.name (상태명 문자열)
  private List<OrderProductDTO> items;

}