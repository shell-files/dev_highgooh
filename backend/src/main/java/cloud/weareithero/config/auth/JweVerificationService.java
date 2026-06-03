package cloud.weareithero.config.auth;

import org.springframework.stereotype.Service;

import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JweVerificationService {

  private final JweKeyManager keyManager;

  public JWTClaimsSet decryptAndValidateToken(String tokenString) throws Exception {
    // 1. 문자열 토큰을 EncryptedJWT 객체로 파싱
    EncryptedJWT jwt = EncryptedJWT.parse(tokenString);

    // 2. 서버의 개인키(Private Key)를 이용해 복호화 진행
    RSADecrypter decrypter = new RSADecrypter(keyManager.getPrivateKey());
    jwt.decrypt(decrypter);

    // 3. Payload(Claims) 추출
    JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();

    /* 4. 만료 시간 등 유효성 검증
    if (claimsSet.getExpirationTime().before(new Date())) {
      throw new RuntimeException("토큰이 만료되었습니다.");
    }
    */

    return claimsSet; // 검증 완료된 클레임 반환
  }

}
