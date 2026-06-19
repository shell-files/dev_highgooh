package cloud.weareithero.auth.domain.file;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

  public Page<FileEntity> findAllByDeleteYn(Boolean deleteYn, Pageable pageable);
  public Optional<FileEntity> findByIdAndDeleteYn(Long id, Boolean deleteYn);

}
