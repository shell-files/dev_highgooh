package cloud.weareithero.api.auth;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.api.auth.dto.UserDTO;
import cloud.weareithero.api.auth.service.AuthService;
import cloud.weareithero.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthContoller implements AuthContollerDocs {

  private final AuthService authService;

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping
  @Override
  public ResponseDTO getAuth() {
    return authService.getAuth();
  }

  @PostMapping
  @Override
  public ResponseDTO postAuth(@RequestBody @Valid UserDTO userDTO, HttpServletResponse response, HttpServletRequest request) {
    return authService.postAuth(userDTO, response, request);
  }

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping
  @Override
  public ResponseDTO deleteAuth(HttpServletResponse response, HttpServletRequest request) {
    return authService.deleteAuth(response, request);
  }

}
