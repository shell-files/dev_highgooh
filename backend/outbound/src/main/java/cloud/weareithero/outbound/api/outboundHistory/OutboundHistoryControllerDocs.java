package cloud.weareithero.outbound.api.outboundHistory;

import org.springframework.web.bind.annotation.RequestBody; // 💡 Spring용 RequestBody만 import

import cloud.weareithero.outbound.api.outboundHistory.dto.OutboundHistoryRequestDTO;
import cloud.weareithero.outbound.docs.ApiCommonErrors;
import cloud.weareithero.outbound.docs.ApiCommonSuccess;
import cloud.weareithero.outbound.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "출고 이력 관리", description = "출고 데이터 조회 및 패킹 현황 대시보드 API")
public interface OutboundHistoryControllerDocs {

  @Operation(summary = "대시보드 패킹 출고 통계 조회", description = "검색 조건에 따른 전체/기간내/지연 패킹 완료 건수를 반환합니다.")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO getDashboardStats(
      // 💡 Swagger용 RequestBody는 import 하지 않고 아래처럼 패키지 풀 경로를 직접 다 적어줍니다.
      @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
          schema = @Schema(implementation = OutboundHistoryRequestDTO.class), 
          examples = {
              @ExampleObject(name = "대시보드 기본 검색 예시", value = OutboundHistoryResponseExamples.DASHBOARD_REQ_DEFAULT, description = "최근 보름간의 출고 패킹 대시보드 통계를 조회합니다.")
          }
      )) 
      @RequestBody OutboundHistoryRequestDTO requestDTO // 💡 Spring용 RequestBody는 간단하게 매핑
  );
}