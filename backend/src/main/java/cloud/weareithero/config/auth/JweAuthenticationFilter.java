package cloud.weareithero.config.auth;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.nimbusds.jwt.JWTClaimsSet;

import cloud.weareithero.api.auth.dto.UserRoleDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JweAuthenticationFilter extends OncePerRequestFilter {

  private final JweVerificationService verificationService;
  private final JweTokenService jweTokenService;

  private final String COOKIE_NAME = "AUTH-TOKEN";
  
  public String checkDomain(String domain) {
    return domain.replace("aigo.", "");
  }

  private void checkCookie(HttpServletRequest request, HttpServletResponse response, String jweToken) {
    // log.info("New Token : {}", jweToken);
    Cookie cookie = new Cookie(COOKIE_NAME, jweToken);
    cookie.setDomain( checkDomain(request.getServerName()) );
    cookie.setPath("/");
    cookie.setMaxAge(-1);
    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    response.addCookie(cookie);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    Cookie authCookie = WebUtils.getCookie(request, COOKIE_NAME);
    String jweToken = (authCookie != null) ? authCookie.getValue() : null;
    if (jweToken != null) {
      try {
        JWTClaimsSet claimsSet = verificationService.decryptAndValidateToken(jweToken);
        // log.info("claimsSet : {}", claimsSet);
        // log.info("id : {}", claimsSet.getLongClaim("id"));
        long id = claimsSet.getLongClaim("id");
        String name = claimsSet.getStringClaim("name");
        String email = claimsSet.getStringClaim("email");
        String role = claimsSet.getStringClaim("role");
        UserRoleDto userRoleDto = UserRoleDto.builder().id(id).name(name).email(email).role(role).build();
        // log.info("User : {}", userRoleDto);

        if (claimsSet.getExpirationTime() != null && claimsSet.getExpirationTime().before(new Date())) {
          String newJweToken = jweTokenService.createToken(userRoleDto);
          checkCookie(request, response, newJweToken);
        }
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(role.split(", "))
          .map(r -> new SimpleGrantedAuthority("ROLE_" + r)).collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userRoleDto, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (ServletException e) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
        return;
      } catch (Exception e) {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

}
