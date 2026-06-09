package cloud.weareithero.api.auth.dto;

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
public class User {

  private String name;
  private String email;
  private String role;

  public static User findByUser(UserRoleDto userRoleDto) {
    return (userRoleDto == null) ? null : User.builder()
      .name(userRoleDto.getName())
      .email(userRoleDto.getEmail())
      .role(userRoleDto.getRole())
      .build();
  }
  
}
