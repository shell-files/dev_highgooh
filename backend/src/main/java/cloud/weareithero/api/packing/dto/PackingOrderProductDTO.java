package cloud.weareithero.api.packing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackingOrderProductDTO {
    
    private int orderProductNo;
    private String productName;
    private int quantity;
    private int price;
    private int totalPrice;
    
}
