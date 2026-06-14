package cloud.weareithero.api.packing.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackingAddDTO {

    private int orderId;
    private List<PackingInvoiceDTO> packingInvoice;
    
}
