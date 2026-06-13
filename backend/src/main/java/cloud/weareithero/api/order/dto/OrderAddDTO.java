package cloud.weareithero.api.order.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * OrderAddDTO - 신규 주문 등록 요청
 *
 * [DB] OUTBOUND 테이블 기준:
 * customerCompanyId → partner_company_id
 * orderDate         → order_date
 * deadline          → deadline (주문마감일자)
 * etd               → etd (출고마감일자)
 */
@Setter @Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "신규 주문 등록 요청 DTO")
public class OrderAddDTO {

  @Positive(message = "고객사 ID를 입력하세요.")
  @Schema(description = "고객사 ID (PARTNER_COMPANY_MASTER.id)", example = "10")
  private int customerCompanyId;

  @NotBlank(message = "주문일자를 입력하세요.")
  @Schema(description = "주문일자 (yyyy-MM-dd)", example = "2026-06-09")
  private String orderDate;

  @NotBlank(message = "주문마감일자를 입력하세요.")
  @Schema(description = "주문마감일자 (yyyy-MM-dd) → OUTBOUND.deadline", example = "2026-06-24")
  private String deadline;

  @NotBlank(message = "출고마감일자를 입력하세요.") // 👈 형식을 맞춘 Validation 가드 추가
  @Schema(description = "출고마감일자 (yyyy-MM-dd) → OUTBOUND.etd", example = "2026-06-25") // 👈 Swagger 문서화 양식 매핑
  private String etd; // 👈 드디어 자바 그릇에 etd 안착!

  @Schema(description = "주문 품목 목록")
  private List<OrderDetailProductDTO> items;

}