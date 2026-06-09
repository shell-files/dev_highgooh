package cloud.weareithero.api.order.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
                
  private int orderId;            //: "PO-20260602-X01",          // 주문 번호
  private int partnerId;          //: "1",                        // 공급사 번호, 발주처(고객사)
  private String partnerName;     //: "(주)한성자재마트",           // 공급사명, 발주처(고객사)
  private String orderProductName;//: "2023 현대 쏘나타 시트레일",   // 제품명
  private int itemCount;          //: 1,200                       // 수량(세트)
  private Long price;              //: 15,000,000                  // 가격(원)}
  private LocalDate orderDate;     //: "2026-06-01"                // 주문 일시;
  private LocalDate deadline;       //: "2026-06-30"               // 납기 일자  
  private LocalDate eta;            //: "2026-06-30"               // 출고 마감 일자  
  private String stateCode;         //: "신규"                     // 주문 상태 코드 (예: ORDER_RECEIVED, IN_PRODUCTION, SHIPPED, DELIVERED 등)

}
