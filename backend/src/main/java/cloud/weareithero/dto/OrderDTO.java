package cloud.weareithero.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    // 1. 주문 중심 정보
    private String orderId;      // 추출할 주문 번호 (없으면 LLM이 생성하도록 유도 가능)
    private String orderDate;    // 주문 날짜 (예: 2026-05-29)

    // 2. 고객 정보 (Customer Node가 될 데이터)
    private CustomerInfo customer;

    // 3. 주문 상품 목록 (Product Node들이 될 데이터 목록)
    private List<ProductInfo> products;

    // --- 하위 static 내부 클래스로 구조화 ---
    @Getter
    @Setter
    @ToString
    public static class CustomerInfo {
      private String customerId; // 고객 ID (이메일이나 전화번호 기반 혹은 고유값)
      private String name;       // 고객 이름
    }

    @Getter
    @Setter
    @ToString
    public static class ProductInfo {
      private String productId;   // 상품 고유 코드
      private String productName; // 상품명
      // private int price;          // 상품 단가
      private int quantity;       // 주문 수량
    }
}
