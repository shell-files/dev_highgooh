package cloud.weareithero.api.carbonAnomaly;

import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyRequestDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyStateUpdateDTO;
import cloud.weareithero.docs.ApiCommonErrors;
import cloud.weareithero.docs.ApiCommonSuccess;
import cloud.weareithero.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "이상치 탐지",
    description = "공정별 탄소배출 이상치 탐지 및 조회 API"
)
public interface CarbonAnomalyControllerDocs {

    @Operation(
        summary = "작일 공정 파이프라인 이상치 현황 조회",
        description = "작일 기준으로 탐지된 공정별 이상치 데이터를 조회합니다."
    )
    @ApiCommonSuccess
    @ApiCommonErrors
    ResponseDTO getYesterdayPipeline();

    @Operation(
        summary = "기간별 공정 이상치 현황 조회",
        description = "연도, 분기, 월 조건에 따라 공정별 이상치 데이터를 조회합니다."
    )
    @ApiCommonSuccess
    @ApiCommonErrors
    ResponseDTO getAnomalyList(
        @RequestBody(
            content = @Content(
                schema = @Schema(
                    implementation = CarbonAnomalyRequestDTO.class
                )
            )
        )
        CarbonAnomalyRequestDTO carbonAnomalyRequestDTO
    );
    @Operation(
        summary = "이상치 조치 상태 변경",
        description = "탐지된 이상치 로그의 조치 상태(대기, 진행중, 완료)를 변경합니다."
    )
    @ApiCommonSuccess
    @ApiCommonErrors
    ResponseDTO updateAnomalyStatus(
        @RequestBody(
            content = @Content(
                schema = @Schema(
                    implementation = CarbonAnomalyStateUpdateDTO.class
                )
            )
        )
        CarbonAnomalyStateUpdateDTO carbonAnomalyStateUpdateDTO
    );

}