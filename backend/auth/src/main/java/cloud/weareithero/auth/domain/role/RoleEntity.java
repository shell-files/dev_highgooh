package cloud.weareithero.auth.domain.role;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import cloud.weareithero.auth.domain.BaseEntity;
import cloud.weareithero.auth.domain.user.UserEntity;

@Entity
@Table(name="ROLE")
@Setter
@Getter
// @ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity extends BaseEntity {
  
  @Column(name = "role", nullable = false, length = 100, unique = true)
  private String role;

  @Column(name = "name", nullable = false, length = 100, unique = true)
  private String name;

  @OrderBy("id asc")
  // @ToString.Exclude
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "USER_ROLE",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private List<UserEntity> users = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", insertable=false, updatable = false)
  private UserEntity createdUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "updated_by", insertable=false, updatable = false)
  private UserEntity updatedUser;

}
