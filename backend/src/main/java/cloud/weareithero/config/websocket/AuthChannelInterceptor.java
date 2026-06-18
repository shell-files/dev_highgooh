package cloud.weareithero.config.websocket;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.nimbusds.jwt.JWTClaimsSet;

import cloud.weareithero.api.auth.dto.UserRoleDto;
import cloud.weareithero.config.auth.JweVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {
    
    private final JweVerificationService verificationService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            
            if (sessionAttributes != null && sessionAttributes.containsKey("AUTH_TOKEN")) {
                String jweToken = (String) sessionAttributes.get("AUTH_TOKEN");
                System.out.println(jweToken);

                // ⭕ [Airflow 전용 예외 처리]
                if ("airflow".equals(jweToken)) {
                    log.info("👑 Airflow 내부 세션인증 우회 적용 - ADMIN 권한 부여");
                    
                    // Airflow 전용 시스템 계정 정보 빌드
                    UserRoleDto airflowSystem = UserRoleDto.builder()
                            .id(0L)
                            .name("Airflow-System")
                            .email("airflow@weareithero.cloud")
                            .role("ADMIN")
                            .build();

                    Collection<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(airflowSystem, null, authorities);
                    
                    accessor.setUser(authentication);
                    return message; // 복호화 단계를 건너뛰고 바로 성공 처리
                }

                try {
                    // ⭕ 기존 JweAuthenticationFilter와 완전히 동일하게 복호화 및 DTO 조립
                    JWTClaimsSet claimsSet = verificationService.decryptAndValidateToken(jweToken);
                    
                    long id = claimsSet.getLongClaim("id");
                    String name = claimsSet.getStringClaim("name");
                    String email = claimsSet.getStringClaim("email");
                    String role = claimsSet.getStringClaim("role");
                    
                    UserRoleDto userRoleDto = UserRoleDto.builder().id(id).name(name).email(email).role(role).build();
                    
                    Collection<? extends GrantedAuthority> authorities = Arrays.stream(role.split(", "))
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r)).collect(Collectors.toList());

                    // 중요: Principal 자리에 문자열이 아닌 유저 DTO 객체를 박제
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userRoleDto, null, authorities);
                    
                    // STOMP 세션 유저로 등록 완료
                    accessor.setUser(authentication);
                    log.info("웹소켓 인증 성공 - 유저 ID: {}, 이메일: {}", id, email);

                } catch (Exception e) {
                    log.error("웹소켓 JWE 토큰 검증 실패: {}", e.getMessage());
                    throw new IllegalArgumentException("Invalid WebSocker Token");
                }
            } else {
                log.error("웹소켓 인증 실패 - 쿠키에 AUTH-TOKEN이 없습니다.");
                throw new IllegalArgumentException("No Token Cookie");
            }
        }
        return message;
    }
}
