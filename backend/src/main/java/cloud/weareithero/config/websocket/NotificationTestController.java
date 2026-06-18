package cloud.weareithero.config.websocket;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
// @RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationTestController {

    private final NotificationService notificationService;


    // ADMIN 그룹에게 실시간 알림을 쏘는 테스트 API
    // @param message 보낼 알림 내용

    // @PostMapping
    @MessageMapping("/chat.send") // 클라이언트는 /app/chat.send로 메시지를 보냄
    // @SendTo("/topic/hg-websocket") 
    public void testAdminNotification(@RequestParam(value = "message") String message) {
        
        // 프론트엔드 Header.jsx가 파싱할 수 있도록 JSON 포맷 문자열로 빌드
        String jsonMessage = String.format("{\"type\":\"critical\", \"message\":\"[테스트] %s\"}", message);
        
        // ⭕ NotificationService를 활용해 /topic/role-ADMIN 채널로 발송
        notificationService.sendToAdminGroup(jsonMessage);
        
        // return ResponseEntity.ok("ADMIN 알림 발송 완료: " + message);    
    }
}
