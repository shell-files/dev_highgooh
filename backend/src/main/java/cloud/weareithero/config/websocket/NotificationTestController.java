package cloud.weareithero.config.websocket;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationTestController {

    private final NotificationService notificationService;

    @MessageMapping("/chat.send")
    public void testAdminNotification(String message) {
        log.info("message : {}", message);
        Map<String, Object> map = new HashMap<>();
        map.put("type", "critical");
        map.put("message", message);
        log.info("map : {}", map);
        notificationService.sendToAdminGroup(map);
    }
}
