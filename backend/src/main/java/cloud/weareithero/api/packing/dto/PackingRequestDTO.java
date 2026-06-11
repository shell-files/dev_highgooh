package cloud.weareithero.api.packing.dto;

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
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "패킹 탭 목록 요청 데이터")
public class PackingRequestDTO extends PageRequestDTO {

    @Schema(description = "주문 번호", defaultValue = "0", example = "0")
    private int orderId;
    @Schema(description = "입고 일자 시작일", defaultValue = "", example = "")
    private String orderStart;
    @Schema(description = "입고 일자 종료일", defaultValue = "", example = "")
    private String orderEnd;
    @Schema(description = "협력사(고객사) 검색", defaultValue = "", example = "")
    private String partnerName;
    // 진행상태(step) : t.OUTBOUND의 step => 7:주문/신규 8:주문/처리중 9:주문/주문완료 19:패킹/패킹중 20:패킹/패킹완료
    // `전체` 선택 시 : step이 9,19,20  /   `신규` 선택 시 : step이 9
    // `처리중` 선택 시 : step이 19     /   `완료` 선택 시 : step이 20
    @Schema(description = "진행 상태 검색", defaultValue = "0", example = "0")
    private String step;

}
