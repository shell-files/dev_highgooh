package cloud.weareithero.outbound.api.packing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackingSummaryDTO {
    
    private int total;
    private int newpacking;
    private int onpacking;
    private int completed;

}
