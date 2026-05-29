package cloud.weareithero.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import cloud.weareithero.dto.ResponseDTO;
import cloud.weareithero.dto.UserDTO;
import cloud.weareithero.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthContoller {

  private final AuthService authService;

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping
  public ResponseDTO getAuth() {
    return authService.getAuth();
  }

  @PostMapping
  public ResponseDTO postAuth(@RequestBody UserDTO userDTO, HttpServletResponse response) {
    return authService.postAuth(userDTO, response);
  }

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping
  public ResponseDTO deleteAuth(HttpServletResponse response) {
    return authService.deleteAuth(response);
  }

}
