package cloud.weareithero.config.websocket;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

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
