package cloud.weareithero.api.packing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackingInvoiceDTO {

    private int id;
    private int orderId;
    private String packingInvoiceNumber;
    private int productId;
    private String productName;
    private int carrierId;
    private String carrierName;
    
}
