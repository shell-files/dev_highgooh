package cloud.weareithero.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {

    Info info = new Info()
      .title("ESG 통합 솔루션 API 명세서")
      .description("SpringBoot 기반 프로젝트의 API 문서입니다.")
      .version("v0.0.1")
      .contact(new Contact()
        .name("W.I.T.H")
        .email("weareithero@gmail.com")
        .url("http://weareithero.cloud"))
      .license(new License()
        .name("Apache License Version 2.0")
        .url("https://www.apache.org/licenses/LICENSE-2.0"));

    Server localServer = new Server().url("http://localhost:8080").description("로컬 개발 서버");
    Server developerServer = new Server().url("http://aigo.myapp.com:8080").description("개발 서버");
    Server prodServer = new Server().url("http://aigo.weareithero.cloud").description("운영 서버");

    String securityJwtName = "JWT 토큰 인증";
    SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityJwtName);
    Components components = new Components()
      .addSecuritySchemes(securityJwtName, new SecurityScheme()
        .name(securityJwtName)
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        .description("임의의 JWT 토큰값을 입력해 주세요. (Bearer 접두사는 자동으로 붙습니다)"));

    return new OpenAPI()
      .info(info)
      .servers(List.of(localServer, developerServer, prodServer))
      .addSecurityItem(securityRequirement)
      .components(components)
      ;
  }

}
