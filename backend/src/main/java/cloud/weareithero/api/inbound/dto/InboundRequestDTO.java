package cloud.weareithero.api.inbound.dto;

import cloud.weareithero.dto.PageRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "입고이력 요청 데이터")
public class InboundRequestDTO extends PageRequestDTO {

    @Schema(description = "asnId", defaultValue = "0", example = "0")
    private int asnId;
    @Schema(description = "주문 기간 시작일", defaultValue = "", example = "")
    private String orderStart;
    @Schema(description = "주문 기간 종료일", defaultValue = "", example = "")
    private String orderEnd;

}
