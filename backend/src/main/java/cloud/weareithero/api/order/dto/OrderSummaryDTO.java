package cloud.weareithero.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummaryDTO {
  
  private int total;
  private int newOrder;
  private int inProgress;
  private int completed;

}
