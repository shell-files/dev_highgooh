package cloud.weareithero.api.order.dto;

import cloud.weareithero.dto.PageRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * OrderRequestDTO - 주문 목록 조회 요청
 */
@Setter @Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주문 목록 조회 요청 데이터")
public class OrderRequestDTO extends PageRequestDTO {

  @Schema(description = "주문 시작일 (yyyy-MM-dd)", defaultValue = "", example = "2026-05-01")
  private String orderStart;

  @Schema(description = "주문 종료일 (yyyy-MM-dd)", defaultValue = "", example = "2026-06-30")
  private String orderEnd;

  @Schema(description = "주문번호(OUTBOUND.id) 부분 검색", defaultValue = "", example = "1")
  private String outboundId;

  @Schema(description = "고객사명 부분 검색", defaultValue = "", example = "한성")
  private String customerName;

  @Schema(description = "진행상태 (신규/처리중/완료)", defaultValue = "", example = "신규")
  private String status;

}