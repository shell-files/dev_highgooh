package cloud.weareithero.api.order.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Order 등록 DTO")
public class OrderAddDTO {

  @Positive(message = "고객사 ID를 입력하세요.")
  private int customerCompanyId;

  @NotBlank(message = "주문 일자를 입력하세요.")
  private String orderDate;

  @NotBlank(message = "주문 마감 일자를 입력하세요.")
  private String deadline;

  @NotBlank(message = "출고 마감 일자를 입력하세요.")
  private String eta;
  
  private List<OrderDetailProductDTO> items;
  
}
