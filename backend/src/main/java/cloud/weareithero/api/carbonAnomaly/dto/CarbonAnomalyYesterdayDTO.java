package cloud.weareithero.api.carbonAnomaly.dto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter
@ToString


public class CarbonAnomalyYesterdayDTO {
    private String process_start;
    private String process;
    
    private double proper_direct_emission;
    private double sum_direct_emission;
    
    private double proper_indirect_emission;
    private double sum_indirect_emission;
    
    private double proper_electricity_used;
    private double sum_electricity_used;
    
    private int total_anomaly_count;
    private int unactioned_anomaly_count; 
}
