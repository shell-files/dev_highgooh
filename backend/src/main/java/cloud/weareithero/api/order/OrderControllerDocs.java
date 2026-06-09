package cloud.weareithero.api.order;

import cloud.weareithero.api.order.dto.OrderAddDTO;
import cloud.weareithero.api.order.dto.OrderRequestDTO;
import cloud.weareithero.docs.ApiCommonErrors;
import cloud.weareithero.docs.ApiCommonSuccess;
import cloud.weareithero.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Order 관리", description = "Order 목록 조회 · Order 생성 · Order 상세 조회 API")
public interface OrderControllerDocs {

  @Operation(summary = "주문 내역 조회", description = "Order API")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO findAll(@RequestBody(
    content = @Content(
      schema = @Schema(implementation = OrderRequestDTO.class),
      examples = {
        @ExampleObject(
            name = "1. 전체 검색 예시",
            value = OrderResponseExamples.SUCCESS1_DEFAULT,
            description = "전체 기간 동안의 주문 내역을 조회할 때 사용합니다."
        ),
        @ExampleObject(
            name = "2. 주문 ID 단건 검색 예시",
            value = OrderResponseExamples.SUCCESS2_DEFAULT,
            description = "날짜 상관없이 특정 주문 번호로만 검색할 때 사용합니다."
        ),
        @ExampleObject(
            name = "3. 날짜 범위 검색 예시",
            value = OrderResponseExamples.SUCCESS3_DEFAULT,
            description = "특정 기간 동안의 주문 내역을 조회할 때 사용합니다."
        ),
        @ExampleObject(
            name = "4. 날짜 범위 및 주문 ID 검색 예시",
            value = OrderResponseExamples.SUCCESS4_DEFAULT,
            description = "특정 기간 동안의 해당 주문 Id 내역을 조회할 때 사용합니다."
        ),
      }
    )) 
    OrderRequestDTO orderRequestDTO);
  
  @Operation(summary = "주문 상세 조회", description = "Order API")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO findOne(Integer orderId);
  
  @Operation(summary = "주문 생성", description = "Order API")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO add(OrderAddDTO orderAddDTO);

  @Operation(summary = "사전출고 통지(Outbound) 정보 조회", description = "Order API")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO findAllOrders();

}
