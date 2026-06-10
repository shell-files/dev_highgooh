package cloud.weareithero.api.outbound.dto;

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
@Schema(description = "출고 요청 데이터")
public class OutboundRequestDTO extends PageRequestDTO {

    @Schema(description = "출고 ID", defaultValue = "0", example = "0")
    private int outboundId;

    @Schema(description = "주문번호 검색어", defaultValue = "", example = "")
    private String orderNo;

    @Schema(description = "고객사명 검색어", defaultValue = "", example = "")
    private String customerName;

    @Schema(description = "진행 상태 코드 (0: 전체)", defaultValue = "0", example = "0")
    private int stateCode;

    @Schema(description = "주문 시작일", defaultValue = "", example = "")
    private String orderStart;

    @Schema(description = "주문 종료일", defaultValue = "", example = "")
    private String orderEnd;

}