package cloud.weareithero.api.outbound;

import cloud.weareithero.api.outbound.dto.OutboundConfirmDTO;
import cloud.weareithero.api.outbound.dto.OutboundInvoiceDTO;
import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;
import cloud.weareithero.api.outbound.dto.OutboundVehicleAssignDTO;
import cloud.weareithero.docs.ApiCommonErrors;
import cloud.weareithero.docs.ApiCommonSuccess;
import cloud.weareithero.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "출고/송장 관리", description = "출고 목록 조회 · 매니페스트 조회 · 차량 배정 · 송장 발급 · 출고 확정 API")
public interface OutboundControllerDocs {

    @Operation(summary = "출고 박스 목록 조회", description = "Outbound API")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO findAll(@RequestBody(
        content = @Content(
            schema = @Schema(implementation = OutboundRequestDTO.class),
            examples = {
                @ExampleObject(
                    name = "1. 전체 검색 예시",
                    value = OutboundResponseExamples.FIND_ALL_DEFAULT,
                    description = "전체 기간 동안의 출고 박스 목록을 조회할 때 사용합니다."
                ),
                @ExampleObject(
                    name = "2. 날짜 범위 + 상태 검색 예시",
                    value = OutboundResponseExamples.FIND_ALL_FILTER,
                    description = "특정 기간 및 진행 상태로 필터링할 때 사용합니다."
                ),
                @ExampleObject(
                    name = "3. 주문번호 검색 예시",
                    value = OutboundResponseExamples.FIND_ALL_BY_ORDER,
                    description = "주문번호로 검색할 때 사용합니다."
                )
            }
        ))
        OutboundRequestDTO outboundRequestDTO);

    @Operation(summary = "출고 주문 상세 조회", description = "Outbound API")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO findOne(Integer outboundId);

    @Operation(summary = "매니페스트 목록 조회", description = "Outbound API")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO findAllManifest(@RequestBody(
        content = @Content(
            schema = @Schema(implementation = OutboundRequestDTO.class),
            examples = {
                @ExampleObject(
                    name = "1. 전체 검색 예시",
                    value = OutboundResponseExamples.FIND_ALL_DEFAULT,
                    description = "전체 매니페스트 목록을 조회합니다."
                )
            }
        ))
        OutboundRequestDTO outboundRequestDTO);

    @Operation(summary = "출고 폼 데이터 조회 (운송사·차량 목록)", description = "Outbound API")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO findAllOutbound();

    @Operation(summary = "차량 배정", description = "Outbound API")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO assignVehicle(OutboundVehicleAssignDTO outboundVehicleAssignDTO);

    @Operation(summary = "송장 발급", description = "Outbound API")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO issueInvoice(OutboundInvoiceDTO outboundInvoiceDTO);

    @Operation(summary = "출고 확정", description = "Outbound API")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO confirmShipment(OutboundConfirmDTO outboundConfirmDTO);

}