package cloud.weareithero.kafka;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaProducer producer;

    @GetMapping("/send")
    public String sendToKafka(@RequestParam("message") String message) {
        producer.sendMessage(message);
        return "Message sent to Kafka successfully!";
    }
    
}
