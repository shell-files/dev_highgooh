package cloud.weareithero.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Order 상세 정보 DTO")
public class OrderDetailProductDTO {
  
  private int no;
  private int orderId;
  private int itemNo; // 제품 PK: ORDER_PRODUCT_LIST의 PK (id)
  private String itemName;
  private int setCount;
  private long price;
}
