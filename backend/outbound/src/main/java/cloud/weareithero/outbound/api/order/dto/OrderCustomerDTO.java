package cloud.weareithero.outbound.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderCustomerDTO - 고객사 기초 데이터 (GET /order 응답 중 customers)
 * PARTNER_COMPANY_MASTER (customer_yn_code = 1) 조회 결과
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCustomerDTO {

  private int id;       // PARTNER_COMPANY_MASTER.id
  private String name;  // PARTNER_COMPANY_MASTER.name

}