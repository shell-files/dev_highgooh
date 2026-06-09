package cloud.weareithero.api.inbound.dto;

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
@Schema(description = "입고 요청 데이터")
public class AsnRequestDTO extends PageRequestDTO {
  
  @Schema(description = "입고 ID", defaultValue = "0", example = "0")
  private int asnId;
  @Schema(description = "입고 시작일", defaultValue = "", example = "")
  private String orderStart;
  @Schema(description = "입고 종료일", defaultValue = "", example = "")
  private String orderEnd;

}
