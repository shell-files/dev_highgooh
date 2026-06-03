package cloud.weareithero.kafka;

import java.util.Map;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

  private final ChatModel chatModel;

  @KafkaListener(topics = "exam-topic", groupId = "my-group")
  public void consume(String message) {
    log.info("← [Consumer] Received message: {}", message);


    String promptMessage = """
      당신은 비정형 텍스트에서 이상 징후를 분석하는 전문가입니다.

      텍스트: {rawText}
    """;

    Prompt prompt = PromptTemplate.builder()
      .template(promptMessage)
      .variables(Map.of("rawText", message))
      .build().create();

    Generation generation = chatModel.call(prompt).getResult();
    log.info("→ [Consumer] Generated response: {}", generation.getOutput().getText());
  }

}
