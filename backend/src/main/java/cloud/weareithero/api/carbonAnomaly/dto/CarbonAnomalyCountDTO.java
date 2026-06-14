package cloud.weareithero.api.carbonAnomaly.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarbonAnomalyCountDTO {
    private String process;
    private long anomaly_count;
}
