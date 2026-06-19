package cloud.weareithero.auth.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  
  UserEntity findByEmailAndDeleteYn(String email, Boolean useYn);
  UserEntity findByEmail(String email);
  UserEntity findByIdAndDeleteYn(Long id, Boolean useYn);

}
