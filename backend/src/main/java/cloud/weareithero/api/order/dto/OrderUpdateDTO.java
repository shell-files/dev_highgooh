package cloud.weareithero.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


//  OrderUpdateDTO - 주문 처리중
@Setter @Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주문 처리중 요청 DTO")
public class OrderUpdateDTO {

  @Schema(description = "출고 예정 일자 / 출고 마감 일자 입력 (yyyy-MM-dd)", example = "2026-07-05")
  private String etd;

}