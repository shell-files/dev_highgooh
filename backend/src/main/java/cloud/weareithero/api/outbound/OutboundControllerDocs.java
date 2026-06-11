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

@Tag(name = "Outbound 출고 관리", description = "박스 목록 조회 · 매니페스트 조회 · 차량 배정 · 송장 발급 · 출고 확정 API")
public interface OutboundControllerDocs {

    @Operation(summary = "출고 박스 목록 조회", description = "OUTBOUND_PACKING 기준 박스 뷰 목록 + 상단 집계 카드 + 페이지네이션")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO findAll(@RequestBody(
        content = @Content(
            schema = @Schema(implementation = OutboundRequestDTO.class),
            examples = {
                @ExampleObject(
                    name = "1. 전체 조회",
                    value = OutboundResponseExamples.FIND_ALL_DEFAULT,
                    description = "조건 없이 전체 박스 목록 조회"
                ),
                @ExampleObject(
                    name = "2. 날짜 + 상태 필터",
                    value = OutboundResponseExamples.FIND_ALL_FILTER,
                    description = "기간 및 상태 필터 조회"
                ),
                @ExampleObject(
                    name = "3. 주문번호 검색",
                    value = OutboundResponseExamples.FIND_ALL_BY_OUTBOUND,
                    description = "주문번호(OUTBOUND.id) 기준 검색"
                )
            }
        ))
        OutboundRequestDTO outboundRequestDTO);

    @Operation(summary = "출고 주문 상세 조회", description = "OUTBOUND 단건 헤더 + ORDER_PRODUCT 목록")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO findOne(Integer outboundId);

    @Operation(summary = "매니페스트 목록 조회", description = "OUTBOUND_TRANSPORTATION 기준 매니페스트 뷰 + 페이지네이션")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO findAllManifest(@RequestBody(
        content = @Content(
            schema = @Schema(implementation = OutboundRequestDTO.class),
            examples = {
                @ExampleObject(
                    name = "1. 전체 조회",
                    value = OutboundResponseExamples.FIND_ALL_DEFAULT,
                    description = "전체 매니페스트 목록 조회"
                )
            }
        ))
        OutboundRequestDTO outboundRequestDTO);

    @Operation(summary = "출고 폼 데이터 조회", description = "차량 배정 모달용 운송사·차량 드롭다운 데이터")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO findAllOutbound();

    @Operation(summary = "차량 배정", description = "OUTBOUND_TRANSPORTATION INSERT 후 OUTBOUND_PACKING 일괄 UPDATE")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO assignVehicle(OutboundVehicleAssignDTO outboundVehicleAssignDTO);

    @Operation(summary = "송장 발급", description = "OUTBOUND_PACKING.invoice_number 저장 + 상태 변경")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO issueInvoice(OutboundInvoiceDTO outboundInvoiceDTO);

    @Operation(summary = "출고 확정", description = "OUTBOUND_TRANSPORTATION 출고완료 처리 + atd 기록")
    @ApiCommonSuccess
    @ApiCommonErrors
    public ResponseDTO confirmShipment(OutboundConfirmDTO outboundConfirmDTO);

}