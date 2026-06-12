package cloud.weareithero.api.carbonEmission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "탄소 배출량 조회 요청 데이터")
public class CarbonEmissionRequestDTO {
    
    @JsonProperty("selectedYear")
    private String year;
    
    @JsonProperty("selectedQuarter")
    private String quarter;
    
    @JsonProperty("selectedMonth")
    private String month;

    // 만약 여전히 메서드를 못 찾는다면, 수동으로 Getter를 추가해보세요.
    public String getMonth() { return this.month; }
    public String getQuarter() { return this.quarter; }
}