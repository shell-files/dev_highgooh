package cloud.weareithero.api.order.dto;

import java.time.LocalDate;

import cloud.weareithero.dto.PageRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주문 조회 요청 데이터")
public class OrderRequestDTO extends PageRequestDTO {
  
  @Schema(description = "주문 시작일", example = "2026-06-01")
  private LocalDate orderStart;
  @Schema(description = "주문 종료일", example = "2026-06-30")
  private LocalDate orderEnd;
  @Schema(description = "주문 번호", defaultValue = "", example = "PO-20260602-X01")
  private String orderNo;
  @Schema(description = "고객사명", defaultValue = "", example = "(주)한성자재마트")
  private String customerName;
  @Schema(description = "진행 상태", defaultValue = "", example = "처리중")
  private String status;

}
