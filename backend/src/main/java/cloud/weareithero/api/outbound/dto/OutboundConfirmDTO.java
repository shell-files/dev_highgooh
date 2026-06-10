package cloud.weareithero.api.outbound.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "출고 확정 요청 DTO")
public class OutboundConfirmDTO {

    @Schema(description = "출고 확정 대상 OUTBOUND ID 목록")
    private List<Integer> outboundIds;

}