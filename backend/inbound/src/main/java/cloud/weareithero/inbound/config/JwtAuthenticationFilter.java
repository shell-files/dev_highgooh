package cloud.weareithero.inbound.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.nimbusds.jwt.SignedJWT;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
   
    private final JwtDecoder jwtDecoder;
    private final StringRedisTemplate redisTemplate;

    public JwtAuthenticationFilter(JwtDecoder jwtDecoder, StringRedisTemplate redisTemplate) {
      this.jwtDecoder = jwtDecoder;
      this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer")) {
          filterChain.doFilter(request,response);
          return;
        }
        final String uuidKey = authHeader.substring(7);
        String savedToken = redisTemplate.opsForValue().get("AT:" +uuidKey);
        try {
          Jwt jwt = jwtDecoder.decode(savedToken);
          setAuthenticationToContext(jwt);
        } catch (JwtException e) {
          log.info("Access Token 만료 혹은 유효하지 않음 -> Refresh Token 검증 시도: {}", e.getMessage()); 
          handleTokenRefresh(response, uuidKey);
        } catch (Exception e) {
          log.info("JWT NULL!!");
        }
        filterChain.doFilter(request,response);
    }
    
    private boolean handleTokenRefresh(HttpServletResponse response, String uuidKey) {
        try {
            String savedToken = redisTemplate.opsForValue().get("RT:" +uuidKey);
            SignedJWT signedJWT = SignedJWT.parse(savedToken);
            Object userId = signedJWT.getJWTClaimsSet().getClaim("userId");
            Object roles = signedJWT.getJWTClaimsSet().getClaim("role");
            Collection<? extends GrantedAuthority> authorities = Arrays.stream(roles.toString().split(",")).map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList());
            log.info("authorities : {}", authorities);
            UserDetails userDetails = new User(userId.toString(), "", authorities);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (JwtException e) {
            log.error("Access Token 생성 중 오류 발생", e);
        } catch (Exception e) {
            log.error("Refresh Token 검증 중 오류 발생", e);
        }
        return false;
    }

    private void setAuthenticationToContext(Jwt jwt) {
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(jwt.getClaims().get("role").toString().split(",")).map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList());
        log.info("authorities : {}", authorities);
        UserDetails userDetails = new User(jwt.getClaims().get("userId").toString(), "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
