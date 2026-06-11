package cloud.weareithero.api.packing;

import cloud.weareithero.api.packing.dto.PackingRequestDTO;
import cloud.weareithero.docs.ApiCommonErrors;
import cloud.weareithero.docs.ApiCommonSuccess;
import cloud.weareithero.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Packing 관리", description = "Packing 목록 조회 · Packing 상세 조회 API")
public interface PackingControllerDocs {

    @Operation(summary = "Packing 목록 조회", description = "Packing API")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO findAll(@RequestBody PackingRequestDTO packingRequestDTO);
    
    
}
