package cloud.weareithero.carbon.api.airflowLog.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AirflowLogRequestDTO {

    private String targetDay;  // "yyyy-MM-dd" 형식
}