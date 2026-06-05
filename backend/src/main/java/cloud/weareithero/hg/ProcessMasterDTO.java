package cloud.weareithero.hg;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessMasterDTO {

    private Long id;
    private String process;
    private Integer process_order;
    private BigDecimal proper_direct_emission;
    private BigDecimal proper_electricity_used;
    private BigDecimal proper_indirect_emission;
    
}
