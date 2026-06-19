package cloud.weareithero.auth.file;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import cloud.weareithero.auth.dto.ResDTO;

public interface FileService {

  public ResDTO upload(MultipartFile file, Authentication authentication);
  public ResponseEntity<?> uri(String type, Long fileId, Authentication authentication);
  public ResponseEntity<?> uri(Long fileId, Authentication authentication);

}
