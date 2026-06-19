package cloud.weareithero.carbon.api.carbonEmission;

import cloud.weareithero.carbon.api.carbonEmission.dto.CarbonEmissionRequestDTO;
import cloud.weareithero.carbon.docs.ApiCommonErrors;
import cloud.weareithero.carbon.docs.ApiCommonSuccess;
import cloud.weareithero.carbon.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "탄소배출량 관리", description = "탄소배출량 통계 및 이력 조회 API")
public interface CarbonEmissionControllerDocs {

    @Operation(summary = "탄소배출량 통계 조회", description = "조건에 따른 탄소배출량 데이터를 조회합니다.")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO getStats(
        @RequestBody(content = @Content(schema = @Schema(implementation = CarbonEmissionRequestDTO.class)
        )) CarbonEmissionRequestDTO carbonEmissionRequestDTO
    );
}