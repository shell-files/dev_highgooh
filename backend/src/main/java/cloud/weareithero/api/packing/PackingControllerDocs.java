package cloud.weareithero.api.packing;

import cloud.weareithero.api.packing.dto.PackingAddDTO;
import cloud.weareithero.api.packing.dto.PackingRequestDTO;
import cloud.weareithero.docs.ApiCommonErrors;
import cloud.weareithero.docs.ApiCommonSuccess;
import cloud.weareithero.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Packing 관리", description = "Packing 목록 조회 · Packing 상세 조회 API")
public interface PackingControllerDocs {

    @Operation(summary = "Packing 목록 조회", description = "Packing API")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO findAll(
        @RequestBody(content = @Content(schema = @Schema(implementation = PackingRequestDTO.class), examples = {
            @ExampleObject(name = "1. 전체 검색 예시", value = PackingResponseExamples.SUCCESS1_DEFAULT, description = "전체 기간 동안의 주문 목록을 조회할 때 사용합니다."),
            @ExampleObject(name = "2. 주문 ID 단건 검색 예시", value = PackingResponseExamples.SUCCESS2_DEFAULT, description = "날짜 상관없이 특정 주문 번호로만 검색할 때 사용합니다."), 
            @ExampleObject(name = "3. 날짜 범위 검색 예시", value = PackingResponseExamples.SUCCESS3_DEFAULT, description = "특정 기간 동안의 주문 내역을 조회할 때 사용합니다."),
            @ExampleObject(name = "4. 날짜 범위 및 주문 ID 검색 예시", value = PackingResponseExamples.SUCCESS4_DEFAULT, description = "특정 기간 동안의 해당 주문 Id 내역을 조회할 때 사용합니다.")
        })) 
    PackingRequestDTO packingRequestDTO);
    
    @Operation(summary = "Packing 상세 조회", description = "Packing API")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO findOne(int packingId);

    @Operation(summary = "Packing 생성", description = "Packing API")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO InsertPacking(
        @RequestBody(content = @Content(schema = @Schema(implementation = PackingAddDTO.class), examples = {
            @ExampleObject(name = "패킹 생성 예시", value = PackingResponseExamples.ADD_PACKING_DEFAULT, description = "addInvoice의 요소 개수에 맞게 송장이 생성됩니다."),
        }
    )) 
    PackingAddDTO packingAddDTO);

    @Operation(summary = "Packing 완료 처리", description = "Packing API")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO completePacking(String packingInvoiceNumber);
    
}
