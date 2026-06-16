package cloud.weareithero.api.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.api.order.dto.OrderAddDTO;
import cloud.weareithero.api.order.dto.OrderRequestDTO;
import cloud.weareithero.api.order.dto.OrderUpdateDTO;
import cloud.weareithero.api.order.service.OrderService;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;

/**
 * Order(출고/주문) Controller
 *
 * [DB 변경] 3차안 기준 주문 헤더 테이블이 ORDER_PRODUCT_LIST → OUTBOUND 로 변경됨
 *           주문 품목 테이블은 ORDER_PRODUCT 유지 (outbound_id FK 참조)
 *
 * HTTP 메서드 규칙 (Inbound 패턴 그대로 유지):
 *   POST   /order           → 목록 조회 (Request Body 필터 전달)
 *   POST   /order/{id}      → 단건 상세 조회
 *   PUT    /order           → 신규 등록
 *   POST   /order/{id}/update → 수정 (Inbound에 없던 신규 엔드포인트)
 *   DELETE /order/{id}      → 삭제 (Inbound에 없던 신규 엔드포인트) 생각해보니 없어도 돼서 일단 주석처리
 *   GET    /order           → 등록 폼 기초 데이터 조회 (고객사·완제품)
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController implements OrderControllerDocs {

  private final OrderService orderService;

  /** 주문 목록 조회 (필터 + 페이지네이션) */
  @PostMapping
  public ResponseDTO findAll(@RequestBody OrderRequestDTO orderRequestDTO) {
    return orderService.findAll(orderRequestDTO);
  }

  /** 주문 단건 상세 조회 */
  @PostMapping("/{outboundId:[0-9]+}")
  public ResponseDTO findOne(@PathVariable Integer outboundId) {
    return orderService.findOne(outboundId);
  }

  /** 신규 주문 등록 */
  @PutMapping
  public ResponseDTO add(@RequestBody OrderAddDTO orderAddDTO) {
    return orderService.add(orderAddDTO);
  }

  /**
   * 주문 수정
   * [신규] Inbound에 없던 수정 엔드포인트
   * Path Variable로 수정 대상 outboundId 전달
   */
  @PatchMapping("/{outboundId:[0-9]+}/update")
  public ResponseDTO update(@PathVariable Integer outboundId,
                            @RequestBody OrderUpdateDTO orderUpdateDTO) {
    return orderService.update(outboundId, orderUpdateDTO);
  }

  /** 등록 모달 기초 데이터 조회 (고객사 목록 + 완제품 목록) */
  @GetMapping
  public ResponseDTO findAllOrders() {
    return orderService.findAllOrders();
  }

}