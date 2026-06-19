package cloud.weareithero.inbound.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Value("${logout.redirect-url}")
  private String redirectUrl;
  
  @Value("${jwt.keys-uri}")
  private String jwkSetURI;

  private final StringRedisTemplate redisTemplate;

  @Bean
  public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withJwkSetUri(jwkSetURI).build();
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable);
    http.cors(Customizer.withDefaults());
    http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    // http.oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()));
    http.authorizeHttpRequests(r -> {
      r.requestMatchers(HttpMethod.GET,"/", "/data").permitAll();
      r.requestMatchers(HttpMethod.GET,"/*.css", "/*.jsx", "/*.json", "/solo_dev.webp").permitAll();
      r.requestMatchers("/docs","/v3/**","/swagger-ui/**").permitAll();
      // r.anyRequest().permitAll();
      r.anyRequest().authenticated();
    });

    JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtDecoder(), redisTemplate);
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

}
