package cloud.weareithero.outbound.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderDetailProductDTO - 주문 품목 상세 (ORDER_PRODUCT 테이블 매핑)
 *
 * [DB 변경] 3차안 ORDER_PRODUCT 컬럼 기준 재정의:
 *   outbound_id          → outboundId  (FK: OUTBOUND.id)
 *   outbound_product_id  → outboundProductId (FK: OUTBOUND_PRODUCT_MASTER.id)
 *   quantity             → quantity
 *   price                → price      (세트당 단가)
 *   total_price          → totalPrice (quantity × price)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주문 품목 상세 DTO (ORDER_PRODUCT 테이블)")
public class OrderDetailProductDTO {

  @Schema(description = "품목 순번 (ORDER_PRODUCT.id)", example = "1")
  private int no;

  @Schema(description = "주문 ID (OUTBOUND.id FK)", example = "10")
  private int outboundId;

  @Schema(description = "완제품 ID (OUTBOUND_PRODUCT_MASTER.id FK)", example = "3")
  private int outboundProductId;

  @Schema(description = "완제품명 (JOIN 표시용)", example = "Al 시트레일 압출재 (6063-T5)")
  private String productName;

  @Schema(description = "주문 수량 (세트)", example = "1200")
  private int quantity;

  @Schema(description = "세트당 단가 (원)", example = "12500")
  private long price;

  @Schema(description = "총 가격 = quantity × price (원)", example = "15000000")
  private long totalPrice;

}