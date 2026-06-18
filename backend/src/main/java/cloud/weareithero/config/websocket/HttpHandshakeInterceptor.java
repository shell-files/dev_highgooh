package cloud.weareithero.config.websocket;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HttpHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            Map<String, String> queryParams = UriComponentsBuilder.fromUri(request.getURI())
                    .build()
                    .getQueryParams()
                    .toSingleValueMap();

            // Airflow가 보낸 특정 파라미터 식별 (?client=airflow)
            if ("airflow".equals(queryParams.get("client"))) {
                log.info("🌐 Airflow 시스템의 웹소켓 연결 시도 감지 - 임시 토큰 발행");
                attributes.put("AUTH_TOKEN", "airflow");
                return true; // 아래 쿠키 검사 생략하고 즉시 핸드셰이크 통과
            }
            
            Cookie[] cookies = httpRequest.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    // 쿠키명 "AUTH-TOKEN" 가로채기
                    if ("AUTH-TOKEN".equals(cookie.getName())) {
                        String jweToken = cookie.getValue();
                        
                        // 웹소켓 세션 Map(attributes)에 임시 임대 저장
                        attributes.put("AUTH_TOKEN", jweToken);
                        break;
                    }
                }
            }
        }
        return true;
    }

}
