package cloud.weareithero.api.inbound;

import cloud.weareithero.api.inbound.dto.InboundRequestDTO;
import cloud.weareithero.docs.ApiCommonErrors;
import cloud.weareithero.docs.ApiCommonSuccess;
import cloud.weareithero.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "입고 이력 관리", description = "입고 이력 목록 조회 · 상세 조회 API")
public interface InboundControllerDocs {
    
    @Operation(summary = "입고 이력 목록 조회", description = "입고 이력 API")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO findAll(
        @RequestBody(content = @Content(schema = @Schema(implementation = InboundRequestDTO.class), examples = {
            @ExampleObject(name = "1. 전체 검색 예시", value = InboundResponseExamples.SUCCESS1_DEFAULT, description = "전체 기간 동안의 입고 내역을 조회할 때 사용합니다."),
            @ExampleObject(name = "2. 입고 ID 단건 검색 예시", value = InboundResponseExamples.SUCCESS2_DEFAULT, description = "날짜 상관없이 특정 입고 번호로만 검색할 때 사용합니다."),
            @ExampleObject(name = "3. 날짜 범위 검색 예시", value = InboundResponseExamples.SUCCESS3_DEFAULT, description = "특정 기간 동안의 입고 내역을 조회할 때 사용합니다."),
            @ExampleObject(name = "4. 날짜 범위 및 입고 ID 검색 예시", value = InboundResponseExamples.SUCCESS4_DEFAULT, description = "특정 기간 동안의 해당 입고 Id 내역을 조회할 때 사용합니다.")
        }
        )) InboundRequestDTO inboundRequestDTO
    );

    @Operation(summary = "입고 상세 조회", description = "입고 이력 API")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO findOne(Integer inboundId);


}
