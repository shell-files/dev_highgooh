package cloud.weareithero.carbon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Rest API 응답")
public class ResponseDTO {

  @Schema(description = "요청 상태", defaultValue = "false")
  private boolean status;
  @Schema(description = "응답 결과", defaultValue = "{}")
  private Object data;
  @Schema(description = "응답 메세지", defaultValue = "유효하지 않은 요청 입니다.")
  private String message;

}
