package cloud.weareithero.api.carbonAnomaly.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarbonAnomalyDTO {

    private Long id;

    private String process;

    private BigDecimal anomaly_score;

    private String state;

    private LocalDateTime create_at;

    private BigDecimal direct_emission;
    private BigDecimal proper_direct_emission;

    private BigDecimal electricity_used;
    private BigDecimal proper_electricity_used;

    private BigDecimal indirect_emission;
    private BigDecimal proper_indirect_emission;
}
