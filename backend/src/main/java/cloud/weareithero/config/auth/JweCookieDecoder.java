package cloud.weareithero.config.auth;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JweCookieDecoder {

  private final JweKeyManager keyManager;
  
  public JWTClaimsSet decodeToken(String tokenString) throws Exception {
    // 1. 문자열을 EncryptedJWT 객체로 파싱
    EncryptedJWT jwt = EncryptedJWT.parse(tokenString);

    // 2. 서버의 복호화용 RSA 개인키(Private Key) 준비
    RSADecrypter decrypter = new RSADecrypter(keyManager.getPrivateKey());

    // 3. 🔑 복호화 진행 (이 단계가 지나면 내부 알맹이가 보입니다)
    jwt.decrypt(decrypter);

    // 4. 내부에 담긴 Payload(Claims) 추출
    JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();

    // 5. 🛡️ 기본적인 만료 시간(Exp) 검증
    if (claimsSet.getExpirationTime() != null && claimsSet.getExpirationTime().before(new Date())) {
      throw new RuntimeException("만료된 토큰입니다.");
    }

    return claimsSet;
  }

}
