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
public class AnomalyReportResponseDTO {

    private BigDecimal anomalyScore;

    private String summary;

    private String estimatedCause;

    private String impactScope;

    private String recommendation;

    private LocalDateTime generatedAt;
}