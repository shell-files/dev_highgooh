package cloud.weareithero.api.order.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * OrderUpdateDTO - 주문 수정 요청
 *
 * OUTBOUND 테이블에서 수정 가능한 컬럼: deadline
 * ORDER_PRODUCT 품목: 전체 재삽입 방식
 *
 * TODO: state_code 수정도 이 DTO에 포함할지 확인 필요
 *       현재는 deadline + items만 수정 가능하도록 정의
 *       (DB: OUTBOUND.state_code는 업무 흐름에 따라 별도 상태 변경 API 가능성)
 */
@Setter @Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주문 수정 요청 DTO")
public class OrderUpdateDTO {

  @Schema(description = "변경할 출고마감일자 (yyyy-MM-dd)", example = "2026-07-05")
  private String deadline;

  @Schema(description = "수정된 품목 목록 (전체 재삽입)")
  private List<OrderDetailProductDTO> items;

}