package cloud.weareithero.config.auth;

// import java.security.KeyPair;
// import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JweKeyManager {
  private final RSAPublicKey publicKey;
  private final RSAPrivateKey privateKey;

  public JweKeyManager(RsaKeyProperties rsaKeys) throws Exception {
      // KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      // keyPairGenerator.initialize(2048);
      // KeyPair keyPair = keyPairGenerator.generateKeyPair();
      // this.publicKey = (RSAPublicKey) keyPair.getPublic();
      // this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
      this.publicKey = (RSAPublicKey) rsaKeys.publicKey();
      this.privateKey = (RSAPrivateKey) rsaKeys.privateKey();
  }

  public RSAPublicKey getPublicKey() { return publicKey; }
  public RSAPrivateKey getPrivateKey() { return privateKey; }

}
