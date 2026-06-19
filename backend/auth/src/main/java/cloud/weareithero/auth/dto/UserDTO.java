package cloud.weareithero.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import cloud.weareithero.auth.domain.role.RoleUserEntity;
import cloud.weareithero.auth.domain.user.UserEntity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

  private Long id;
  private String email;
  private String name;
  private Boolean deleteYn;
  private String createdBy;
  private String updatedBy;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "ASIA/Seoul")
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "ASIA/Seoul")
  private LocalDateTime updatedAt;

  private List<RoleDTO> roles;

  public static UserDTO findByUser(UserEntity userEntity) {
    List<RoleDTO> arr = new ArrayList<>();
    userEntity.getRoles().forEach(role -> {
      /*
      boolean check = true;
      for(RoleUserEntity ru : userEntity.getRoleUsers()) {
        if(role.getId() == ru.getRoleId()) {
          if(ru.getDeleteYn()) check = false;
        }
      }
      if(check) {
        }
        */
      RoleDTO roleDTO = RoleDTO.builder()
          .no(role.getId())
          .name(role.getName())
          .role(role.getRole())
          .build();
      arr.add(roleDTO);
    });
    return (userEntity == null) ? null : UserDTO.builder()
        .id(userEntity.getId())
        .email(userEntity.getEmail())
        .name(userEntity.getName())
        .deleteYn(userEntity.getDeleteYn())
        .roles(arr)
        .createdAt(userEntity.getCreatedAt())
        .createdBy((userEntity.getCreatedBy() == null) ? null : userEntity.getCreatedUser().getName())
        .updatedAt(userEntity.getUpdatedAt())
        .updatedBy((userEntity.getUpdatedBy() == null) ? null : userEntity.getUpdatedUser().getName())
        .build();
  }

  public static Map<String, Object> findByUsers(Page<UserEntity> userEntities) {
    Map<String, Object> resultMap = new HashMap<>();
    List<UserDTO> users = new ArrayList<>();
    userEntities.forEach(user -> users.add(UserDTO.findByUser(user)));
    resultMap.put("list", users);
    resultMap.put("totalElements", userEntities.getTotalElements());
    resultMap.put("totalPages", userEntities.getTotalPages());
    resultMap.put("size", userEntities.getSize());
    return resultMap;
  }
  
}
