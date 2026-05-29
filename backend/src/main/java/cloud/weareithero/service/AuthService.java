package cloud.weareithero.service;

import cloud.weareithero.dto.ResponseDTO;
import cloud.weareithero.dto.UserDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
  
  public ResponseDTO getAuth();
  public ResponseDTO postAuth(UserDTO userDTO, HttpServletResponse response);
  public ResponseDTO deleteAuth(HttpServletResponse response);

}
