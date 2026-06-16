package cloud.weareithero.api.order;

import cloud.weareithero.api.order.dto.OrderAddDTO;
import cloud.weareithero.api.order.dto.OrderRequestDTO;
import cloud.weareithero.api.order.dto.OrderUpdateDTO;
import cloud.weareithero.docs.ApiCommonErrors;
import cloud.weareithero.docs.ApiCommonSuccess;
import cloud.weareithero.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Order 관리", description = "주문 목록 조회 · 주문 등록 · 주문 상세 조회 · 주문 수정 · 주문 삭제 API")
public interface OrderControllerDocs {

  @Operation(summary = "주문 목록 조회",
      description = "주문기간·주문번호·고객사명·진행상태 조건으로 주문 목록을 페이지네이션 조회합니다. (OUTBOUND 테이블 기반)")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO findAll(@RequestBody(
      content = @Content(
          schema = @Schema(implementation = OrderRequestDTO.class),
          examples = {
              @ExampleObject(
                  name = "1. 전체 검색",
                  value = OrderResponseExamples.SUCCESS1_DEFAULT,
                  description = "전체 주문 내역을 조회합니다."
              ),
              @ExampleObject(
                  name = "2. 주문번호 검색",
                  value = OrderResponseExamples.SUCCESS2_DEFAULT,
                  description = "특정 주문번호(outbound_id)로 검색합니다."
              ),
              @ExampleObject(
                  name = "3. 날짜 범위 검색",
                  value = OrderResponseExamples.SUCCESS3_DEFAULT,
                  description = "특정 기간의 주문 내역을 조회합니다."
              ),
              @ExampleObject(
                  name = "4. 고객사명 + 날짜 복합 검색",
                  value = OrderResponseExamples.SUCCESS4_DEFAULT,
                  description = "날짜 범위와 고객사명을 복합 조건으로 검색합니다."
              ),
              @ExampleObject(
                  name = "5. 진행상태 검색",
                  value = OrderResponseExamples.SUCCESS5_DEFAULT,
                  description = "진행상태(신규/처리중/완료)로 필터링합니다."
              ),
          }
      ))
      OrderRequestDTO orderRequestDTO);

  @Operation(summary = "주문 상세 조회",
      description = "outboundId로 주문 마스터 정보(OUTBOUND)와 품목 목록(ORDER_PRODUCT)을 조회합니다.")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO findOne(Integer outboundId);

  @Operation(summary = "신규 주문 등록",
      description = "고객사·주문일자·출고마감일·품목 목록을 입력받아 OUTBOUND + ORDER_PRODUCT 에 저장합니다.")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO add(@RequestBody(
      content = @Content(
          schema = @Schema(implementation = OrderRequestDTO.class),
          examples = {
              @ExampleObject(
                  name = "신규 주문 등록",
                  value = OrderResponseExamples.ORDER_ADD_DEFAULT,
                  description = "신규 주문을 등록합니다."
              )})) OrderAddDTO orderAddDTO);

  @Operation(summary = "주문 수정",
      description = "기존 주문의 마감일·진행상태·품목 수량·금액을 수정합니다. (OUTBOUND 헤더 + ORDER_PRODUCT 품목)")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO update(Integer outboundId, OrderUpdateDTO orderUpdateDTO);

  @Operation(summary = "주문 등록 기초 데이터 조회",
      description = "신규 주문 등록 모달에 필요한 고객사 목록(PARTNER_COMPANY_MASTER)과 완제품 목록(OUTBOUND_PRODUCT_MASTER)을 조회합니다.")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO findAllOrders();

}