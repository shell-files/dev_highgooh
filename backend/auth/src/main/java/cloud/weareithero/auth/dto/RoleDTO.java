package cloud.weareithero.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import cloud.weareithero.auth.domain.role.RoleEntity;
import cloud.weareithero.auth.domain.user.UserEntity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
  
  private Long no;
  private String name;
  private String role;
  private List<UserDTO> users;
  private String createdUserName;
  private String updatedUserName;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "ASIA/Seoul")
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "ASIA/Seoul")
  private LocalDateTime updatedAt;

  public static RoleDTO findByRole(RoleEntity roleEntity) {
    List<UserEntity> userEntities = roleEntity.getUsers();
    List<UserDTO> users = new ArrayList<>();
    if(userEntities != null) {
      userEntities.forEach(user -> users.add(UserDTO.findByUser(user)));
    }
    return (roleEntity == null) ? null : RoleDTO.builder()
        .no(roleEntity.getId())
        .name(roleEntity.getName())
        .role(roleEntity.getRole())
        .users(users)
        .createdAt(roleEntity.getCreatedAt())
        .createdUserName((roleEntity.getCreatedBy() == null) ? null : roleEntity.getCreatedUser().getName())
        .updatedAt(roleEntity.getUpdatedAt())
        .updatedUserName((roleEntity.getUpdatedBy() == null) ? null : roleEntity.getUpdatedUser().getName())
        .build();
  }

  public static Map<String, Object> findByRole(List<RoleEntity> roleEntities) {
    Map<String, Object> resultMap = new HashMap<>();
    List<RoleDTO> roles = new ArrayList<>();
    roleEntities.forEach(role -> roles.add(RoleDTO.findByRole(role)));
    resultMap.put("list", roles);
    return resultMap;
  }

}
