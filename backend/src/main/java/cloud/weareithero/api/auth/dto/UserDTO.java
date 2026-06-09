package cloud.weareithero.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "사용자 정보 DTO")
public class UserDTO {
  
  @NotBlank(message = "이메일을 입력하세요.")
  private String email;

  @NotBlank(message = "비밀번호를 입력하세요.")
  private String password;
  
}
