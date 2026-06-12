package cloud.weareithero.api.outbound.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 주문 상세 모달 제품 목록 1행
 *
 * 기준 테이블: ORDER_PRODUCT (3차안 기준)
 *   id, outbound_id, outbound_product_id, quantity, price, total_price
 *
 * JOIN: OUTBOUND_PRODUCT_MASTER (제품명)
 *
 * ※ DB에 ORDER_PRODUCT.box_id 없음 → boxQty 산출 불가 (JSX dummyOrderDetails의 boxQty 화면 표시 불일치)
 * ※ invoice_number는 ORDER_PRODUCT 레벨이 아닌 OUTBOUND_PACKING 레벨에 존재
 *    (JSX dummyOrderDetails의 invoiceNo는 DB와 레벨 불일치 - 팀 협의 필요)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutboundProductDTO {

    private int productId;          // ORDER_PRODUCT.id
    private String productName;     // OUTBOUND_PRODUCT_MASTER.name
    private int quantity;           // ORDER_PRODUCT.quantity (수량, 세트)
    private long price;             // ORDER_PRODUCT.price (세트당 가격)
    private long totalPrice;        // ORDER_PRODUCT.total_price (총 가격)

    // ※ 아래 필드는 DB에 직접 대응 컬럼 없음 → 현재 구현 제외, 필요 시 팀 협의
    // private int boxQty;          // JSX에 존재하나 ORDER_PRODUCT에 box_id 없음
    // private String invoiceNo;    // ORDER_PRODUCT 레벨에 없음, OUTBOUND_PACKING에 존재

}