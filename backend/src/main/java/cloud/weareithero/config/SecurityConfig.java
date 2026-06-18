package cloud.weareithero.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import cloud.weareithero.config.auth.JweAuthenticationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JweAuthenticationFilter jweAuthenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // http.csrf(csrf -> csrf.disable());
    http.csrf(AbstractHttpConfigurer::disable);
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
    http.authorizeHttpRequests(authorize -> {
      authorize.requestMatchers("/docs", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();
      authorize.requestMatchers(HttpMethod.POST, "/auth").permitAll();
      authorize.requestMatchers("/hg-websocket/**").permitAll();
      authorize.anyRequest().authenticated();
      // authorize.anyRequest().permitAll();
    });
    http.addFilterBefore(jweAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // 모든 출처(Origin) 허용 (스웨거 테스트 목적)
    configuration.setAllowedOriginPatterns(List.of("*"));
    // 허용할 HTTP 메서드 규칙
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    // 허용할 헤더 규칙
    configuration.setAllowedHeaders(List.of("*"));
    // 자격 증명(쿠키, 인증 헤더 등) 허용 여부
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    // 모든 URL 경로에 위의 CORS 정책 적용
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}