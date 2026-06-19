package cloud.weareithero.auth.config.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 특정 사용자에게 1:1 실시간 알림 전송
     * @param userEmail 알림을 받을 유저의 이메일 (UserRoleDto 내의 email 필드가 식별 기준이 됨)
     * @param message 알림 창에 띄울 내용 객체 또는 텍스트
     */
    public void sendNotification(String userEmail, Object message) {
        // 스프링 내부적으로 /user/{userEmail}/queue/notifications 주소로 매핑하여 저격 전송함
        log.info("sendNotification : {}, {}", userEmail, message);
        messagingTemplate.convertAndSendToUser(userEmail, "/queue/notifications", message);
    }

    // 모든 관리자 전용 채널로 메시지 쏘기
    public void sendToAdminGroup(Object message) {
        log.info("sendToAdminGroup : {}", message);
        messagingTemplate.convertAndSend("/topic/role-ADMIN", message);
    }
    
    // 모든 MANAGER 유저 전용 채널로 메시지 쏘기
    public void sendToManagerGroup(Object message) {
        log.info("sendToManagerGroup : {}", message);
        messagingTemplate.convertAndSend("/topic/role-MANAGER", message);
}

}
