package cloud.weareithero.config.auth;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import cloud.weareithero.api.auth.dto.UserRoleDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JweTokenService {

  @Value("${token.expire.access}")
  private int accessExpire;

  @Value("${token.expire.refresh}")
  private int refreshExpire;

  private final JweKeyManager keyManager;

  private String generatorToken(UserRoleDto userRoleDto, long timeoutInMs) throws JOSEException {
    // 1. 토큰에 담을 Claims(Payload) 생성
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
      .subject("highgooh-authentication")
      .claim("id", userRoleDto.getId())
      .claim("name", userRoleDto.getName())
      .claim("email", userRoleDto.getEmail())
      .claim("role", userRoleDto.getRole())
      .expirationTime(new Date(System.currentTimeMillis() + timeoutInMs))
      .issueTime(new Date())
      .build();

    // 2. JWE 헤더 설정 (알고리즘: RSA-OAEP-256, 암호화 방식: AES-GCM)
    JWEHeader header = new JWEHeader(
      JWEAlgorithm.RSA_OAEP_256,
      EncryptionMethod.A256GCM
    );

    // 3. EncryptedJWT 객체 생성
    EncryptedJWT jwt = new EncryptedJWT(header, claimsSet);

    // 4. 공개키(Public Key)를 이용해 암호화 진행
    RSAEncrypter encrypter = new RSAEncrypter(keyManager.getPublicKey());
    jwt.encrypt(encrypter);

    // 5. 직렬화하여 문자열 토큰 반환 (이 값은 외부에 노출되어도 복호화 전엔 내용을 알 수 없음)
    return jwt.serialize();
  }
  
  /***************************************
  HttpSession session
  int sessionTimeoutInSeconds = session.getMaxInactiveInterval();
  long sessionTimeoutInMs = sessionTimeoutInSeconds * 1000L;
  ***************************************/

  public String createToken(UserRoleDto userRoleDto) throws JOSEException {
    long sessionTimeoutInMs = accessExpire * 1000L;
    return generatorToken(userRoleDto, sessionTimeoutInMs);
  }

  public String createRefresh(UserRoleDto userRoleDto) throws JOSEException {
    long sessionTimeoutInMs = refreshExpire * 1000L;
    return generatorToken(userRoleDto, sessionTimeoutInMs);
  }

}
