package cloud.weareithero.auth.domain.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

  public Page<RoleEntity> findAllByDeleteYn(Boolean deleteYn, Pageable pageable);
  public List<RoleEntity> findAllByDeleteYn(Boolean deleteYn);
  public Optional<RoleEntity> findByIdAndDeleteYn(Long id, Boolean deleteYn);

}
