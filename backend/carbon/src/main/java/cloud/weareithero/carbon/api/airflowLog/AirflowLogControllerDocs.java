package cloud.weareithero.carbon.api.airflowLog;

import cloud.weareithero.carbon.api.airflowLog.dto.AirflowLogRequestDTO;
import cloud.weareithero.carbon.docs.ApiCommonErrors;
import cloud.weareithero.carbon.docs.ApiCommonSuccess;
import cloud.weareithero.carbon.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "AI 이상치 보고서",
    description = "AIRFLOW 기반 AI 이상치 분석 보고서 조회 API"
)
public interface AirflowLogControllerDocs {

    @Operation(
        summary = "날짜별 AI 이상치 보고서 조회",
        description = "지정한 날짜(target_day) 기준으로 AI가 생성한 이상치 분석 보고서를 조회합니다. 날짜를 지정하지 않으면 오늘 날짜로 조회합니다."
    )
    @ApiCommonSuccess
    @ApiCommonErrors
    ResponseDTO getAirflowLog(
        @RequestBody(
            content = @Content(
                schema = @Schema(
                    implementation = AirflowLogRequestDTO.class
                )
            )
        )
        AirflowLogRequestDTO airflowLogRequestDTO
    );
}