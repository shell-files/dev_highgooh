package cloud.weareithero.carbon.api.carbonEmission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CarbonEmissionDTO {
    private String date; 
    
    // 쿼리에서 `AS process`로 가져오는 값
    private String process; 
    
    // 배출량 데이터
    private Double indirect_emission;
    private Double direct_emission;
    private Double total_emission;
}
