package cloud.weareithero.api.aiReport.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyReportContextDTO {

    private Long anomalyLogId;

    private String process;

    private BigDecimal anomalyScore;

    private String state;

    private LocalDateTime detectedAt;

    private BigDecimal directEmission;
    private BigDecimal properDirectEmission;

    private BigDecimal indirectEmission;
    private BigDecimal properIndirectEmission;

    private BigDecimal electricityUsed;
    private BigDecimal properElectricityUsed;
}