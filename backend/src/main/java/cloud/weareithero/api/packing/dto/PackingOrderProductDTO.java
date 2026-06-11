package cloud.weareithero.api.packing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackingOrderProductDTO {
    
    private int orderProductNo;         //	주문 상세 table pk
    private int productId;              //	제품 pk
    private String productName;         //  제품명
    private int quantity;                   
    private int price;
    private int totalPrice;
    
}
