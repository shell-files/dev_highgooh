package cloud.weareithero.api.order.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cloud.weareithero.api.order.dao.OrderDao;
import cloud.weareithero.api.order.dto.OrderAddDTO;
import cloud.weareithero.api.order.dto.OrderCustomerDTO;
import cloud.weareithero.api.order.dto.OrderDetailProductDTO;
import cloud.weareithero.api.order.dto.OrderDTO;
import cloud.weareithero.api.order.dto.OrderProductDTO;
import cloud.weareithero.api.order.dto.OrderRequestDTO;
import cloud.weareithero.api.order.dto.OrderSummaryDTO;
import cloud.weareithero.api.order.dto.OrderUpdateDTO;
import cloud.weareithero.dto.PaginationDTO;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Order ServiceImpl
 *
 * [DB 변경] OUTBOUND 테이블이 주문 헤더
 *           ORDER_PRODUCT 테이블이 주문 품목 (outbound_id FK)
 *
 * Inbound 패턴 완전 유지:
 *   - try-catch + isSuccess + ResponseDTO.builder() 패턴
 *   - 복합 등록: OUTBOUND INSERT → useGeneratedKeys → ORDER_PRODUCT 반복 INSERT
 *   - 로그: log.info() 레벨
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
  //    Inbound add() 복합 등록 패턴 동일:
  //    OUTBOUND INSERT → useGeneratedKeys로 outboundId 획득
  //    → ORDER_PRODUCT 반복 INSERT → 건수 비교
  // ─────────────────────────────────────────────
  @Override
  public ResponseDTO add(OrderAddDTO orderAddDTO) {
    boolean isSuccess = false;
    String message = null;
    Map<String, Object> request = new HashMap<>();
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      int itemCount = (orderAddDTO.getItems() == null) ? 0 : orderAddDTO.getItems().size();

      // OUTBOUND 헤더 INSERT용 DTO 구성
      // [DB 변경] ORDER_PRODUCT_LIST → OUTBOUND, state_code 미포함(DB DEFAULT 의존)
      OrderDTO orderDTO = OrderDTO.builder()
          .partnerCompanyId(orderAddDTO.getCustomerCompanyId())
          .orderDate(LocalDate.parse(orderAddDTO.getOrderDate(), formatter))
          .deadline(LocalDate.parse(orderAddDTO.getDeadline(), formatter))
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
            //   outbound_id FK = 방금 생성된 outboundId
            //   price, total_price 모두 클라이언트 전달값 저장
            //   (서버 재계산 여부는 팀 정책에 따라 결정 필요)
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
      message = "주문 등록에 실패했습니다.";
    }
    return ResponseDTO.builder()
        .status(isSuccess)
        .data(request)
        .message(message)
        .build();
  }

  // ─────────────────────────────────────────────
  // 4. 주문 수정
  //    [신규] Inbound에 없던 기능
  //    OUTBOUND 헤더(deadline, state_code) 수정
  //    ORDER_PRODUCT 품목 수정 (quantity, price, total_price)
  //
  //    TODO: 품목 수정 전략 결정 필요
  //          현재는 "기존 품목 전체 삭제 후 재삽입" 방식으로 구현
  //          항목별 UPDATE 방식이 필요하면 orderDao.updateOrderProduct() 별도 추가
  // ─────────────────────────────────────────────
@Override
  public ResponseDTO update(int outboundId, OrderUpdateDTO orderUpdateDTO) {
    boolean isSuccess = false;
    String message = null;
    Map<String, Object> request = new HashMap<>();
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      // OUTBOUND 헤더 수정용 DTO 구성 (기한 변경만 반영)
      OrderDTO orderDTO = OrderDTO.builder()
          .outboundId(outboundId)
          .deadline(orderUpdateDTO.getDeadline() != null
              ? LocalDate.parse(orderUpdateDTO.getDeadline(), formatter)
              : null)
          .build();
      
      // DB 업데이트 실행
      orderDao.update(orderDTO);

      isSuccess = true;
      message = "주문 정보가 성공적으로 수정되었습니다.";
    } catch (Exception e) {
      log.error("OrderServiceImp update error : {}", e.getMessage());
      message = "주문 수정에 실패했습니다.";
    }
    
    return ResponseDTO.builder()
        .status(isSuccess)
        .data(request)
        .message(message)
        .build();
  }

      // 품목 수정: 기존 품목 전체 삭제 후 재삽입
  //     if (orderUpdateDTO.getItems() != null && !orderUpdateDTO.getItems().isEmpty()) {
  //       orderDao.deleteOrderProducts(outboundId);
  //       int size = 0;
  //       for (OrderDetailProductDTO item : orderUpdateDTO.getItems()) {
  //         OrderDetailProductDTO detailDTO = OrderDetailProductDTO.builder()
  //             .outboundId(outboundId)
  //             .outboundProductId(item.getOutboundProductId())
  //             .quantity(item.getQuantity())
  //             .price(item.getPrice())
  //             .totalPrice(item.getQuantity() * item.getPrice())
  //             .build();
  //         size += orderDao.addOrderProduct(detailDTO);
  //       }
  //       // TODO: 부분 실패 시 롤백이 필요하다면 @Transactional 적용 검토
  //       //       현재 프로젝트는 @Transactional 미사용 원칙이나, 삭제+재삽입 복합 작업이므로 확인 필요
  //       log.info("OrderServiceImp update items inserted : {}", size);
  //     }

  //     isSuccess = true;
  //     message = "주문 수정이 완료되었습니다.";
  //   } catch (Exception e) {
  //     log.info("OrderServiceImp update error : {}", e.getMessage());
  //     message = "주문 수정에 실패했습니다.";
  //   }
  //   return ResponseDTO.builder()
  //       .status(isSuccess)
  //       .data(request)
  //       .message(message)
  //       .build();
  // }

  // ─────────────────────────────────────────────
  // 5. 주문 삭제
  //    [신규] Inbound에 없던 기능
  //    ORDER_PRODUCT 품목 먼저 삭제 후 OUTBOUND 헤더 삭제 (FK 순서)
  //    TODO: OUTBOUND_PACKING, OUTBOUND_TRANSPORT 등 연관 테이블
  //          데이터가 있는 주문의 삭제 정책 결정 필요
  // ─────────────────────────────────────────────
  // @Override
  // public ResponseDTO delete(int outboundId) {
  //   boolean isSuccess = false;
  //   String message = null;
  //   Map<String, Object> request = new HashMap<>();
  //   try {
  //     // 1. ORDER_PRODUCT 품목 삭제 (FK 제약 순서)
  //     orderDao.deleteOrderProducts(outboundId);
  //     // 2. OUTBOUND 헤더 삭제
  //     orderDao.delete(outboundId);
  //     isSuccess = true;
  //     message = "주문 삭제가 완료되었습니다.";
  //   } catch (Exception e) {
  //     log.info("OrderServiceImp delete error : {}", e.getMessage());
  //     message = "주문 삭제에 실패했습니다.";
  //   }
  //   return ResponseDTO.builder()
  //       .status(isSuccess)
  //       .data(request)
  //       .message(message)
  //       .build();
  // }

  // ─────────────────────────────────────────────
  // 6. 등록 모달 기초 데이터 조회
  //    Inbound findAllAsn() 패턴 동일
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