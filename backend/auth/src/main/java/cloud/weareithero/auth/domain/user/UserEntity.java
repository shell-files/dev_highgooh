package cloud.weareithero.auth.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import cloud.weareithero.auth.domain.BaseEntity;
import cloud.weareithero.auth.domain.role.RoleEntity;
import cloud.weareithero.auth.domain.role.RoleUserEntity;

@Entity
@Table(name="USER")
@Setter
@Getter
// @ToString
// @ToString(exclude = "roleUsers")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends BaseEntity {
  
  @Column(name = "email", nullable = false, length = 100, unique = true)
  private String email;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "password", nullable = true)
  private String password;

  @Column(name = "file_id", nullable = true)
  private Long fileId;

  @OrderBy("id asc")
  // @ToString.Exclude
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "USER_ROLE",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<RoleEntity> roles = new HashSet<>();

  // @ToString.Exclude
  @OneToMany(mappedBy = "targetUser")
  private Set<RoleUserEntity> roleUsers = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", insertable=false, updatable = false)
  private UserEntity createdUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "updated_by", insertable=false, updatable = false)
  private UserEntity updatedUser;

}
