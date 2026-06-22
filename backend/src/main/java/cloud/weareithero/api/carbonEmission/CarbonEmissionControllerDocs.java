package cloud.weareithero.api.carbonEmission;

import cloud.weareithero.api.carbonEmission.dto.CarbonEmissionRequestDTO;
import cloud.weareithero.docs.ApiCommonErrors;
import cloud.weareithero.docs.ApiCommonSuccess;
import cloud.weareithero.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "탄소배출량 관리", description = "탄소배출량 통계 및 이력 조회 API")
public interface CarbonEmissionControllerDocs {

    @Operation(summary = "탄소배출량 통계 조회", description = "조건에 따른 탄소배출량 데이터를 조회합니다.")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO getStats(
        @RequestBody(
            content = @Content(
                schema = @Schema(implementation = CarbonEmissionRequestDTO.class),
                examples = {
                    @ExampleObject(
                        name = "1. 연도별 조회",
                        value = CarbonEmissionResponseExamples.SUCCESS_YEAR,
                        description = "특정 연도의 전체 통계를 조회합니다."
                    ),
                    @ExampleObject(
                        name = "2. 분기별 조회",
                        value = CarbonEmissionResponseExamples.SUCCESS_QUARTER,
                        description = "특정 연도의 특정 분기 통계를 조회합니다."
                    ),
                    @ExampleObject(
                        name = "3. 월별 조회",
                        value = CarbonEmissionResponseExamples.SUCCESS_MONTH,
                        description = "특정 연도의 특정 월 통계를 조회합니다."
                    )
                }
            )
        ) CarbonEmissionRequestDTO carbonEmissionRequestDTO
    );
}