package cloud.weareithero.api.outbound.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 출고 확정 요청 DTO
 *
 * 매니페스트 탭에서 체크박스로 선택한 OUTBOUND_TRANSPORTATION.id 목록을 받아
 * state_code → 출고완료, atd = NOW() UPDATE
 *
 * ※ 매니페스트 단위 확정 (박스 단위 아님)
 * ※ JSX 매니페스트 탭 [출고 확정] 버튼 → checkedManifests → transportationIds 로 전달
 */
@Setter @Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "출고 확정 요청 DTO")
public class OutboundConfirmDTO {

    @Schema(description = "확정 대상 OUTBOUND_TRANSPORTATION.id 목록 (매니페스트 ID)")
    private List<Integer> transportationIds;

}