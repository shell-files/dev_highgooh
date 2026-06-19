package cloud.weareithero.carbon.api.airflowLog.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AirflowLogDTO {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime target_day;

    private String summary;

    private String reasoning;

    private String recommendation;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime create_at;
}