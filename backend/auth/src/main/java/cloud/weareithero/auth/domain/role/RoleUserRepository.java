package cloud.weareithero.auth.domain.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleUserRepository extends JpaRepository<RoleUserEntity, Long> {

  public Page<RoleUserEntity> findAllByDeleteYn(Boolean deleteYn, Pageable pageable);
  public List<RoleUserEntity> findAllByDeleteYn(Boolean deleteYn);
  public Optional<RoleUserEntity> findByIdAndDeleteYn(Long id, Boolean deleteYn);
   
}