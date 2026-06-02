package cloud.weareithero.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.dto.OrderDTO;
import cloud.weareithero.dto.OrdersDTO;
import cloud.weareithero.neo4j.OrderStatementService;
import cloud.weareithero.service.AiService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {
  
  private final AiService aiService;
  private final OrderStatementService orderStatementService;

  @GetMapping
  public OrderDTO chat() {
    String rawText = """
        일시: 2026년 5월 29일
        접수자: 김지환 (고객 고유코드: CUST-9991)
        내용:
        '빌릿(상품코드: PROD-1104)' 6개
        '빌릿(상품코드: PROD-3304)' 3개        
        주문번호는 'ORD-20260529-03'
    """;

    return aiService.chat(rawText);
  }

  @PostMapping
  public OrdersDTO getOrder(@RequestParam("name") String name) {
    return orderStatementService.generateAutomatedStatement(name);
  }
}
