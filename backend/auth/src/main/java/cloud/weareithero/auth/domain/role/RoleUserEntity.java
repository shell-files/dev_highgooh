package cloud.weareithero.auth.domain.role;

import jakarta.persistence.*;
import lombok.*;

import cloud.weareithero.auth.domain.BaseEntity;
import cloud.weareithero.auth.domain.user.UserEntity;

@Entity
@Table(name="USER_ROLE")
@Setter
@Getter
// @ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleUserEntity extends BaseEntity {
  
  @Column(name = "role_id", nullable = false)
  private Long roleId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", insertable=false, updatable = false)
  private UserEntity targetUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", insertable=false, updatable = false)
  private UserEntity createdUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "updated_by", insertable=false, updatable = false)
  private UserEntity updatedUser;

}
