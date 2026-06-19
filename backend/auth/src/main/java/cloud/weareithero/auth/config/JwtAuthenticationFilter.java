package cloud.weareithero.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import cloud.weareithero.auth.domain.user.UserEntity;
import cloud.weareithero.auth.domain.user.UserRepository;
import cloud.weareithero.auth.dto.UserDTO;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
// @Component
// @RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private Integer accessExpire;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(Integer accessExpire, JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, 
                                   StringRedisTemplate redisTemplate, UserRepository userRepository) {
        this.accessExpire = accessExpire;
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
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

    private void handleTokenRefresh(HttpServletResponse response, String uuidKey) {
        try {
            String savedToken = redisTemplate.opsForValue().get("RT:" +uuidKey);
            if(savedToken == null) return;
            Jwt decodedRefreshToken = jwtDecoder.decode(savedToken);
            String email = decodedRefreshToken.getSubject();
            UserEntity userEntity = userRepository.findByEmailAndDeleteYn(email, false);
            if(userEntity == null) {
              log.info("유효하지 않은 사용자 입니다.");
              return;
            }
            UserDTO userDTO = UserDTO.findByUser(userEntity);
            Set<String> arr = new HashSet<>();
            userDTO.getRoles().forEach(d -> arr.add(d.getName()));
            String roles = String.join(",", arr);
            JwtClaimsSet accessClaimsSet = JwtClaimsSet.builder()
                .issuer("Authorization_Server")
                .subject(userDTO.getEmail())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(accessExpire, ChronoUnit.MINUTES))
                .claim("role", roles)
                .claim("userId", userDTO.getId())
                .build();
            JwtEncoderParameters accessParameters = JwtEncoderParameters.from(accessClaimsSet);
            String access_token = jwtEncoder.encode(accessParameters).getTokenValue();

            String uuid_key = UUID.randomUUID().toString();
            log.info("uuid_key : {}", uuid_key);
            redisTemplate.opsForValue().set("AT:" + uuid_key, access_token, accessExpire, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set("RT:" + uuid_key, savedToken);

            Cookie cookie = new Cookie("access_token", uuid_key);
            cookie.setHttpOnly(true);
            // cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(-1);
            response.addCookie(cookie);  
            log.info("Refresh Token 검증 완료. 새로운 Access Token을 발급합니다.");

            Jwt decodedAccesshToken = jwtDecoder.decode(access_token);
            setAuthenticationToContext(decodedAccesshToken);
            log.info("새로운 Access Token이 발급되어 헤더에 세팅되었습니다.");
        } catch (JwtException e) {
            log.error("Access Token 생성 중 오류 발생", e);
        } catch (Exception e) {
            log.error("Refresh Token 검증 중 오류 발생", e);
        }
    }

    private void setAuthenticationToContext(Jwt jwt) {
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(jwt.getClaims().get("role").toString().split(",")).map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList());
        log.info("authorities : {}", authorities);
        UserDetails userDetails = new User(jwt.getClaims().get("userId").toString(), "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
