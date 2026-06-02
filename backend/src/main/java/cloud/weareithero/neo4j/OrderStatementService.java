package cloud.weareithero.neo4j;

import java.util.List;
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
import cloud.weareithero.dto.OrdersDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatementService {

  private final OrderGraphRepository orderGraphRepository;
  private final ChatModel chatModel;

  @Transactional
  public OrdersDTO generateAutomatedStatement(String customerName) {

    StructuredOutputConverter<OrdersDTO> outputConverter = new BeanOutputConverter<>(OrdersDTO.class);
    String format = outputConverter.getFormat();

    // 1. 지식 그래프(Neo4j)에서 해당 고객의 얽혀있는 주문 내역 관계망을 가져옴
    List<OrderNode> orderHistory = orderGraphRepository.findFullOrderHistory(customerName);

    if (orderHistory == null || orderHistory.isEmpty()) {
      log.warn("⚠ [경고] Neo4j DB에서 고객 [{}]의 주문 내역 그래프를 찾을 수 없습니다. 쿼리나 DB 데이터를 확인하세요.", customerName);
      return null;
    }

    // 2. 그래프 데이터를 텍스트 컨텍스트로 변환
    StringBuilder contextBuilder = new StringBuilder();
    for (OrderNode order : orderHistory) {
      contextBuilder.append(String.format("- 주문번호(orderId): %s (날짜: %s)\n", order.getOrderId(), order.getOrderDate()));
      for (ContainsEdge edge : order.getProducts()) { 
        ProductNode product = edge.getProduct(); // 관계 엔티티로부터 TargetNode 추출
        int quantity = edge.getQuantity();       // 관계 선에 저장된 수량 추출
        
        contextBuilder.append(String.format("  ▶ 상품ID: %s | 상품명: %s | 수량: %d개\n", 
          product.getProductId(), product.getProductName(), quantity));
      }
    }

    // 3. Ollama 프롬프트 작성 (지식 그래프 컨텍스트 주입)
    String promptMessage = """
      당신은 신뢰할 수 있는 자산 및 주문 관리 시스템입니다.
      아래 제공된 '지식 그래프 기반 주문 컨텍스트'만을 바탕으로 고객에게 보낼 공식 [주문 내역 보고서] 서식으로 작성해주세요.
      그 외의 상상해낸 정보는 절대 포함하지 마십시오.
      
      [고객명]: {customerName}
      
      [지식 그래프 기반 주문 컨텍스트]
      {context}
      
      응답 형식:
      {format}
    """;

    log.info("지식그래프 : {}", contextBuilder.toString());

    // 4. 내역서 생성
    Prompt prompt = PromptTemplate.builder()
      .template(promptMessage)
      .variables(Map.of("customerName", customerName, "context", contextBuilder.toString(), "format", format))
      .build().create();

    Generation generation = chatModel.call(prompt).getResult();
    String rawResponse = generation.getOutput().getText();
    log.info("Ollama 원본 응답: {}", rawResponse);

    try {
      return outputConverter.convert(rawResponse);
    } catch (Exception e) {
      log.error("AI 응답을 OrdersDTO로 파싱하는 중 예외 발생. 원본 데이터: {}", rawResponse, e);
      throw new RuntimeException("주문 데이터 생성 중 포맷 오류가 발생했습니다.", e);
    }
  }
  
  @Transactional
  public void saveOrderDtoToGraph(OrderDTO dto) {

    log.info("1. Customer Node 생성 및 매핑");
    CustomerNode customerNode = new CustomerNode();
    customerNode.setCustomerId(dto.getCustomer().getCustomerId());
    customerNode.setName(dto.getCustomer().getName());

    log.info("2. Order Node 생성 및 매핑 (Customer 바인딩)");
    OrderNode orderNode = new OrderNode();
    orderNode.setOrderId(dto.getOrderId());
    orderNode.setOrderDate(dto.getOrderDate());
    orderNode.setCustomer(customerNode);

    log.info("3. Product Nodes 생성 및 매핑 (List에 추가)");
    for (OrderDTO.ProductInfo productInfo : dto.getProducts()) {
      ProductNode productNode = new ProductNode();
      productNode.setProductId(productInfo.getProductId());
      productNode.setProductName(productInfo.getProductName());
      // productNode.setQuantity(productInfo.getQuantity());
      // productNode.setPrice(productInfo.getPrice());

      ContainsEdge containsEdge = new ContainsEdge();
      containsEdge.setQuantity(productInfo.getQuantity());
      containsEdge.setProduct(productNode);
      
      orderNode.getProducts().add(containsEdge);
    }

    log.info("4. Neo4j DB에 저장");
    log.info("하위 노드(Customer, Product)와 관계(ORDERED_BY, CONTAINS)가 한 번에 Graph 형태로 생성됩니다.");
    orderGraphRepository.save(orderNode);
    log.info("지식 그래프에 주문 내역 저장 완료! 주문번호: {}", orderNode.getOrderId());
  }

}
