package cloud.weareithero.api.packing.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PackingDTO {

    private int orderId;
    private int partnerId;
    private String partnerName;
    private int totalSets;
    private LocalDate orderDate;
    private LocalDate deadline;
    private LocalDate etd;
    private int stepCode;
    private String step;
    
}
