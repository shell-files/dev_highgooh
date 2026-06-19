package cloud.weareithero.outbound.api.order.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import cloud.weareithero.outbound.api.order.dao.OrderDao;
import cloud.weareithero.outbound.api.order.dto.OrderAddDTO;
import cloud.weareithero.outbound.api.order.dto.OrderCustomerDTO;
import cloud.weareithero.outbound.api.order.dto.OrderDTO;
import cloud.weareithero.outbound.api.order.dto.OrderDetailProductDTO;
import cloud.weareithero.outbound.api.order.dto.OrderProductDTO;
import cloud.weareithero.outbound.api.order.dto.OrderRequestDTO;
import cloud.weareithero.outbound.api.order.dto.OrderSummaryDTO;
import cloud.weareithero.outbound.api.order.dto.OrderUpdateDTO;
import cloud.weareithero.outbound.dto.PaginationDTO;
import cloud.weareithero.outbound.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Order ServiceImpl
 *
 * [DB 변경] OUTBOUND 테이블이 주문 헤더
 * ORDER_PRODUCT 테이블이 주문 품목 (outbound_id FK)
 *
 * Inbound 패턴 완전 유지:
 * - try-catch + isSuccess + ResponseDTO.builder() 패턴
 * - 복합 등록: OUTBOUND INSERT → useGeneratedKeys → ORDER_PRODUCT 반복 INSERT
 * - 로그: log.info() 레벨
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {

  private final OrderDao orderDao;

  // ─────────────────────────────────────────────
  // 1. 주문 목록 조회 (필터 + 페이지네이션)
  // ─────────────────────────────────────────────
  @Override
  public ResponseDTO findAll(OrderRequestDTO orderRequestDTO) {
    boolean isSuccess = false;
    String message = null;
    Map<String, Object> request = new HashMap<>();
    try {
      OrderSummaryDTO summary = orderDao.findSummary(orderRequestDTO);
      List<OrderDTO> orderList = orderDao.findAll(orderRequestDTO);
      PaginationDTO pagination = PaginationDTO.builder()
          .page(orderRequestDTO.getPage())
          .totalCount(summary.getTotal())
          .totalPages((int) Math.ceil((double) summary.getTotal() / orderRequestDTO.getSize()))
          .build();
      request.put("summary", summary);
      request.put("list", orderList);
      request.put("pagination", pagination);
      isSuccess = true;
      message = "주문 내역 조회가 완료되었습니다.";
    } catch (Exception e) {
      log.info("OrderServiceImp findAll error : {}", e.getMessage());
      message = "주문 내역 조회에 실패했습니다.";
    }
    return ResponseDTO.builder()
        .status(isSuccess)
        .data(request)
        .message(message)
        .build();
  }

  // ─────────────────────────────────────────────
  // 2. 주문 상세 조회
  // ─────────────────────────────────────────────
  @Override
  public ResponseDTO findOne(int outboundId) {
    boolean isSuccess = false;
    String message = null;
    Map<String, Object> request = new HashMap<>();
    try {
      OrderDTO order = orderDao.findByOutboundId(outboundId);
      List<OrderDetailProductDTO> items = orderDao.findOne(outboundId);
      request.put("order", order);
      request.put("items", items);
      isSuccess = true;
      message = "주문 상세 조회가 완료되었습니다.";
    } catch (Exception e) {
      log.info("OrderServiceImp findOne error : {}", e.getMessage());
      message = "주문 상세 조회에 실패했습니다.";
    }
    return ResponseDTO.builder()
        .status(isSuccess)
        .data(request)
        .message(message)
        .build();
  }

  // ─────────────────────────────────────────────
  // 3. 신규 주문 등록
  // Inbound add() 복합 등록 패턴 동일:
  // OUTBOUND INSERT → useGeneratedKeys로 outboundId 획득
  // → ORDER_PRODUCT 반복 INSERT → 건수 비교
  // ─────────────────────────────────────────────
  @Override
  @Transactional
  public ResponseDTO add(OrderAddDTO orderAddDTO) {
    boolean isSuccess = false;
    String message = null;
    Map<String, Object> request = new HashMap<>();
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      int itemCount = (orderAddDTO.getItems() == null) ? 0 : orderAddDTO.getItems().size();

      // 💡 [수정] 1. 하위 제품 항목들을 돌며 총 주문 수량(totalQuantity) 먼저 계산
      int totalQty = 0;
      if (itemCount > 0) {
        for (OrderDetailProductDTO item : orderAddDTO.getItems()) {
          totalQty += item.getQuantity();
        }
      }

      // OUTBOUND 헤더 INSERT용 DTO 구성
      // 💡 [수정] 2. etd 파싱 추가, 계산한 totalQuantity 주입, 초기 상태 코드(7: 주문접수) 주입 완료
      OrderDTO orderDTO = OrderDTO.builder()
          .partnerCompanyId(orderAddDTO.getCustomerCompanyId())
          // .orderDate(LocalDate.parse(orderAddDTO.getOrderDate(), formatter))
          .deadline(LocalDate.parse(orderAddDTO.getDeadline(), formatter))
          .etd(orderAddDTO.getEtd() != null && !orderAddDTO.getEtd().isEmpty()
              ? LocalDate.parse(orderAddDTO.getEtd(), formatter)
              : null)
          .totalQuantity(totalQty)
          .stateCode("7") // 👈 목록 조회 BETWEEN 7 AND 9 조건에 부합하도록 초기값 '7' 강제 부여
          .build();

      // OUTBOUND INSERT (useGeneratedKeys → orderDTO.outboundId에 PK 주입)
      orderDao.add(orderDTO);

      if (orderDTO.getOutboundId() > 0) {
        isSuccess = true;
        message = "주문 등록이 완료되었습니다.";

        if (itemCount > 0) {
          int size = 0;
          for (OrderDetailProductDTO item : orderAddDTO.getItems()) {
            // [DB 변경] ORDER_PRODUCT INSERT:
            // outbound_id FK = 방금 생성된 outboundId
            // price, total_price 모두 클라이언트 전달값 저장
            OrderDetailProductDTO detailDTO = OrderDetailProductDTO.builder()
                .outboundId(orderDTO.getOutboundId())
                .outboundProductId(item.getOutboundProductId())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .totalPrice(item.getQuantity() * item.getPrice())
                .build();
            size += orderDao.addOrderProduct(detailDTO);
          }
          if (size != itemCount) {
            isSuccess = false;
            message = "주문 등록이 일부 실패했습니다.";
          }
        }
      }
    } catch (Exception e) {
      log.info("OrderServiceImp add error : {}", e.getMessage());
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      message = "주문 등록에 실패했습니다.";
    }
    return ResponseDTO.builder()
        .status(isSuccess)
        .data(request)
        .message(message)
        .build();
  }

  // ─────────────────────────────────────────────
  // 4. 주문 완료
  // OUTBOUND 헤더(etd, state_code) 수정
  // ─────────────────────────────────────────────
  @Override
  @Transactional
  public ResponseDTO update(int outboundId, OrderUpdateDTO orderUpdateDTO) {
    boolean isSuccess = false;
    String message = null;
    try {
      int result = orderDao.update(outboundId, orderUpdateDTO.getEtd());
      if (result > 0) {
          isSuccess = true;
          message = "주문이 완료되었습니다.";
      } else {
          message = "해당 주문을 찾을 수 없습니다.";
      }
    } catch (Exception e) {
      log.info("OrderServiceImp update error : {}", e.getMessage());
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      message = "주문 상태 변경에 실패했습니다.";
    }
    return ResponseDTO.builder()
      .status(isSuccess)
      .message(message)
      .build();
  }

  // ─────────────────────────────────────────────
  // 6. 등록 모달 기초 데이터 조회
  // Inbound findAllAsn() 패턴 동일
  // ─────────────────────────────────────────────
  @Override
  public ResponseDTO findAllOrders() {
    boolean isSuccess = false;
    String message = null;
    Map<String, Object> request = new HashMap<>();
    try {
      List<OrderCustomerDTO> customers = orderDao.findByCustomer();
      List<OrderProductDTO> products = orderDao.findByProduct();
      request.put("customers", customers);
      request.put("products", products);
      isSuccess = true;
      message = "주문 등록 기초 데이터 조회가 완료되었습니다.";
    } catch (Exception e) {
      log.info("OrderServiceImp findAllOrders error : {}", e.getMessage());
      message = "주문 등록 기초 데이터 조회에 실패했습니다.";
    }
    return ResponseDTO.builder()
        .status(isSuccess)
        .data(request)
        .message(message)
        .build();
  }

}