package cloud.weareithero.api.auth.service;

import cloud.weareithero.api.auth.dto.UserDTO;
import cloud.weareithero.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
  
  public ResponseDTO getAuth();
  public ResponseDTO postAuth(UserDTO userDTO, HttpServletResponse response, HttpServletRequest request);
  public ResponseDTO deleteAuth(HttpServletResponse response, HttpServletRequest request);

}
