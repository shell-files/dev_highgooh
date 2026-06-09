package cloud.weareithero.api.inbound.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InboundItemDTO {
    
    private int no;
    private int inboundId;
    private int itemNo;
    private String itemName;
    private String alloyType;
    private double weight;
}
