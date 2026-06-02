package cloud.weareithero.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaProducerImp implements KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendMessage(String message) {
      System.out.println(String.format("→ [Producer] Sending message: %s", message));
      this.kafkaTemplate.send("exam-topic", message);
    }

}
