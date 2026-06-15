package cloud.weareithero.api.carbonAnomaly.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CarbonAnomalyRequestDTO {

    @JsonProperty("selectedYear")
    private String year;

    @JsonProperty("selectedQuarter")
    private String quarter;

    @JsonProperty("selectedMonth")
    private String month;

    // 페이징 파라미터 추가
    private Integer page = 1; 
    
    // MyBatis SQL에서 사용할 계산된 변수들
    private Integer limit = 10;
    private Integer offset;       // DB에서 건너뛸 행의 개수
}