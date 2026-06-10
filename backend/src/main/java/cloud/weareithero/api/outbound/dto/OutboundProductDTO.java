package cloud.weareithero.api.outbound.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 주문 상세 모달 제품 목록 1행
 * 대상 테이블: ORDER_PRODUCT JOIN OUTBOUND_PRODUCT_MASTER JOIN PRODUCT_PRICE
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutboundProductDTO {

    private int productId;       // OUTBOUND_PRODUCT_MASTER.id
    private String productName;  // OUTBOUND_PRODUCT_MASTER.name
    private int quantity;        // ORDER_PRODUCT.quantity
    private long totalPrice;     // ORDER_PRODUCT.total_price

    private int boxQty;          // OUTBOUND_PACKING.total_box_quantity 사용
    private String invoiceNo;    // OUTBOUND_TRANSPORT.invoice_number 사용

}