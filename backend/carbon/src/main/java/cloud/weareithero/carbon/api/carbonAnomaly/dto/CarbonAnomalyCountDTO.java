package cloud.weareithero.carbon.api.carbonAnomaly.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarbonAnomalyCountDTO {
    private String process;
    private long anomaly_count;
    private long actioned_count;
    private long actioning_count;
    private long waiting_count;

}
