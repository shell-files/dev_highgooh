package cloud.weareithero.api.auth.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.weareithero.api.auth.dao.AuthMapper;
import cloud.weareithero.api.auth.dto.TokenDTO;
import cloud.weareithero.api.auth.dto.User;
import cloud.weareithero.api.auth.dto.UserDTO;
import cloud.weareithero.api.auth.dto.UserRoleDto;
import cloud.weareithero.config.auth.JweTokenService;
import cloud.weareithero.dto.ResponseDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

  private final JweTokenService jweTokenService;
  private final AuthMapper authMapper;
  private final BCryptPasswordEncoder passwordEncoder;

  private final String COOKIE_NAME = "AUTH-TOKEN";
  
  public String checkDomain(String domain) {
    return domain.replace("aigo.", "");
  }

  @Override
  public ResponseDTO getAuth() {
    Boolean status = false;
    User user = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      // log.info("Authentication: {}", authentication);
      UserRoleDto principal = (UserRoleDto) authentication.getPrincipal();
      Optional<UserRoleDto> userOptional = authMapper.findByEmail(principal.getEmail());
      if (userOptional.isPresent()) {
        UserRoleDto userRoleDto = userOptional.get();
        user = User.findByUser(userRoleDto);
      }
      status = true;
    }
    return ResponseDTO.builder()
      .status(status)
      .data(user)
      .message(null)
      .build();
  }

  @Override
  public ResponseDTO postAuth(UserDTO userDTO, HttpServletResponse response, HttpServletRequest request) {
    Boolean status = false;
    try {
      // log.info("UserDTO : {}", userDTO);

      Optional<UserRoleDto> userOptional = authMapper.findByEmail(userDTO.getEmail());

      if (userOptional.isPresent()) {
        UserRoleDto userRoleDto = userOptional.get();

        if(passwordEncoder.matches(userDTO.getPassword(), userRoleDto.getPassword())) {
          String refresh_token = jweTokenService.createRefresh(userRoleDto);
          TokenDTO tokenDTO = TokenDTO.builder().refresh_token(refresh_token).build();
          if(authMapper.saveToken(tokenDTO) > 0) {
            // log.info("Token ID : {}", tokenDTO);
            userRoleDto.setId(tokenDTO.getId());
          }

          String jweToken = jweTokenService.createToken(userRoleDto);
          // log.info("Token : {}", jweToken);

          Cookie cookie = new Cookie(COOKIE_NAME, jweToken);
          cookie.setDomain(  checkDomain(request.getServerName()) );
          cookie.setPath("/");
          cookie.setMaxAge(-1);
          // cookie.setMaxAge(60 * 30);
          cookie.setHttpOnly(true);
          cookie.setSecure(false);
          response.addCookie(cookie);

          status = true;
        }
      } 
      
    } catch (Exception e) {
      log.info("토큰 발급에 실패했습니다: {}", e.getMessage());
    }
    return ResponseDTO.builder()
      .status(status)
      .data(null)
      .message(null)
      .build();
  }

  @Override
  public ResponseDTO deleteAuth(HttpServletResponse response, HttpServletRequest request) {
    Boolean status = false;
    try{
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.isAuthenticated()) {
        // log.info("Authentication: {}", authentication);
        UserRoleDto principal = (UserRoleDto) authentication.getPrincipal();
        // log.info("Token ID: {}", principal.getId());
        authMapper.delToken(principal.getId());

        Cookie cookie = new Cookie(COOKIE_NAME, null);
        cookie.setDomain( checkDomain(request.getServerName()) );
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        response.addCookie(cookie);

        status = true;
      }
    } catch (Exception e) {
      log.info("토큰 삭제에 실패했습니다: {}", e.getMessage());
    } finally {
      SecurityContextHolder.clearContext();
    }
    return ResponseDTO.builder()
      .status(status)
      .data(null)
      .message(null)
      .build();
    
  }
  
}
