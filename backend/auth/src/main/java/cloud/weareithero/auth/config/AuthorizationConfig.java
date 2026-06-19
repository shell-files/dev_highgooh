package cloud.weareithero.auth.config;

import cloud.weareithero.auth.domain.user.UserRepository;
import cloud.weareithero.auth.oauth.OAuthClientService;

import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class AuthorizationConfig {

    @Value("${logout.redirect-url}")
    private String redirectUrl;

    @Value("${token.expire.access}")
    private Integer accessExpire;

    private final String KEYID = "public-key-id";

    private final OAuthClientService oAuthClientService;
    private final RsaKeyProperties rsaKeys;

    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    
    @Bean
    @Order(1)
    SecurityFilterChain authorizationFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();
        http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher());
        http.with(authorizationServerConfigurer, Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(Customizer.withDefaults());
        http.authorizeHttpRequests(r -> r.anyRequest().authenticated());
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(Customizer.withDefaults());
        // http.httpBasic(Customizer.withDefaults());
        http.formLogin(Customizer.withDefaults());
        http.logout(logout -> {
          logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"));
          logout.invalidateHttpSession(true);
          logout.clearAuthentication(true);
          logout.logoutSuccessHandler((req, res, auth) -> {
            Cookie[] cookies = req.getCookies();
            if(cookies != null) {
              for(int i = 0; i < cookies.length; i++) {
                cookies[i].setMaxAge(0);
                cookies[i].setPath("/");
                res.addCookie(cookies[i]);
              }
            }
            res.sendRedirect(redirectUrl);
          });
          logout.permitAll();
        });
        http.authorizeHttpRequests(r -> {
            r.requestMatchers(HttpMethod.GET,"/","/.well-known/jwks.json").permitAll();
            r.requestMatchers("/auth/**", "/file/**").permitAll();
            r.requestMatchers("/docs","/v3/**","/swagger-ui/**").permitAll();
            r.anyRequest().authenticated();
        });
        
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
          accessExpire, jwtEncoder(), jwtDecoder(), redisTemplate, userRepository
        );        
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> originUris = List.of(redirectUrl);
        originUris.forEach(config::addAllowedOrigin);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
      return new RegisteredClientRepository() {
        @Override
        public void save(RegisteredClient registeredClient) {}
        @Override
        public RegisteredClient findById(String id) {
          return oAuthClientService.findById(id);
        }
        @Override
        public RegisteredClient findByClientId(String clientId) {
          return oAuthClientService.findByClientId(clientId);
        }
      };
    }

    @Bean
    public JWKSet jwkSet() {
      RSAKey.Builder builder = new RSAKey.Builder(rsaKeys.publicKey())
        .keyUse(KeyUse.ENCRYPTION)
        .algorithm(JWEAlgorithm.RSA_OAEP_256)
        .keyID(KEYID);
      return new JWKSet(builder.build());
    }

    @Bean
    public JwtEncoder jwtEncoder() {
      JWK jwk = new RSAKey.Builder(rsaKeys.publicKey())
        .privateKey(rsaKeys.privateKey())
        .keyID(KEYID)
        .build();
      JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
      return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
      return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
      return (context -> {
        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
          RegisteredClient client = context.getRegisteredClient();
          JwtClaimsSet.Builder builder = context.getClaims();
          builder.issuer("Oauth2_Server");
          builder.expiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
          builder.claims((claims) -> {
              claims.put("scope", client.getScopes());
          });
          builder.claim("username", client.getClientName());
          builder.claim("userNo", client.getId());
        }
      });
    }

}
