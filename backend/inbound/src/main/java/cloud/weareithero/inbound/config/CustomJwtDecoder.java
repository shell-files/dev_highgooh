package cloud.weareithero.inbound.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Component
public class CustomJwtDecoder implements JwtDecoder {

    private final NimbusJwtDecoder nimbusJwtDecoder;
    private final StringRedisTemplate redisTemplate;

    public CustomJwtDecoder(@Value("${jwt.keys-uri}") String jwkSetURI, StringRedisTemplate redisTemplate) {
        this.nimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetURI).build();
        this.redisTemplate = redisTemplate;
    }
  
    @Override
    public Jwt decode(String token) throws JwtException {
        String savedActualToken = redisTemplate.opsForValue().get("AT:" + token);
        log.info("Access Token : {}", savedActualToken);
        if (savedActualToken == null) {
            savedActualToken = redisTemplate.opsForValue().get("RT:" + token);
            log.info("Refresh Token : {}", savedActualToken);
            if (savedActualToken == null) {
                throw new JwtValidationException("토큰이 만료되었으며 재발급에 실패했습니다.", null);
            }
        }
        try {
            return nimbusJwtDecoder.decode(savedActualToken);
        } catch (Exception e) {
            throw new BadJwtException("유효하지 않은 JWT 토큰입니다.", e);
        }
    }
  
}
