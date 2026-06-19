package cloud.weareithero.outbound.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderProductDTO - 완제품 기초 데이터 (GET /order 응답 중 products)
 * OUTBOUND_PRODUCT_MASTER 전체 조회 결과
 * 등록 모달 품목 드롭다운 초기화용
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductDTO {

  private int productId;       // OUTBOUND_PRODUCT_MASTER.id
  private String productName;  // OUTBOUND_PRODUCT_MASTER.name
  private int price;           // PRODUCT_PRICE.price

}