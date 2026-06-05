package cloud.weareithero.service;

import java.util.Map;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.StructuredOutputConverter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.weareithero.dto.OrderDTO;
import cloud.weareithero.neo4j.OrderStatementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional()
@RequiredArgsConstructor
public class AiServiceImp implements AiService {

  private final ChatModel chatModel;

  private final OrderStatementService orderStatementService;

  @Override
  public OrderDTO chat(String rawText) {
    StructuredOutputConverter<OrderDTO> outputConverter = new BeanOutputConverter<>(OrderDTO.class);
    String format = outputConverter.getFormat();
    String promptMessage = """
      당신은 비정형 텍스트에서 주문 정보를 추출하는 전문가입니다.
      다음 텍스트에서 고객 정보, 주문 정보, 상품 정보를 추출하여 지식 그래프 형태로 변환해주세요.
      
      텍스트: {rawText}
      
      응답 형식:
      {format}
    """;
        
    Prompt prompt = PromptTemplate.builder()
      .template(promptMessage)
      .variables(Map.of("rawText", rawText, "format", format))
      .build().create();

    Generation generation = chatModel.call(prompt).getResult();
    OrderDTO orderDTO = outputConverter.convert(generation.getOutput().getText());
    log.info("Ollama가 분석 후 매핑해 줄 결과: {}", orderDTO);

    orderStatementService.saveOrderDtoToGraph(orderDTO);
    return orderDTO;
  }
  
}
