package cloud.weareithero.api.order.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cloud.weareithero.api.order.dao.OrderDao;
import cloud.weareithero.api.order.dto.OrderAddDTO;
import cloud.weareithero.api.order.dto.OrderCustomerDTO;
import cloud.weareithero.api.order.dto.OrderDTO;
import cloud.weareithero.api.order.dto.OrderDetailProductDTO;
import cloud.weareithero.api.order.dto.OrderProductDTO;
import cloud.weareithero.api.order.dto.OrderRequestDTO;
import cloud.weareithero.api.order.dto.OrderSummaryDTO;
import cloud.weareithero.dto.PaginationDTO;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {

  private final OrderDao orderDao;

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
      message = "Order 조회가 완료되었습니다.";
    } catch (Exception e) {
      log.info("OrderServiceImp findAll error : {}", e.getMessage());
      message = "Order 조회에 실패했습니다.";
    }
    return ResponseDTO.builder()
        .status(isSuccess)
        .data(request)
        .message(message)
        .build();
  }

  @Override
  public ResponseDTO findOne(int orderId) {
    boolean isSuccess = false;
    String message = null;
    Map<String, Object> request = new HashMap<>();
    try {
      OrderDTO order = orderDao.findByOrderId(orderId);
      List<OrderDetailProductDTO> orderProducts = orderDao.findOne(orderId);
      request.put("order", order);
      request.put("items", orderProducts);
      isSuccess = true;
      message = "Order 상세 정보 조회가 완료되었습니다.";
    } catch (Exception e) {
      log.info("OrderServiceImp findOne error : {}", e.getMessage());
      message = "Order 상세 정보 조회가 실패했습니다.";
    }
    return ResponseDTO.builder()
        .status(isSuccess)
        .data(request)
        .message(message)
        .build();
  }

  @Override
  public ResponseDTO add(OrderAddDTO orderAddDTO) {
    boolean isSuccess = false;
    String message = null;
    Map<String, Object> request = new HashMap<>();
    try {
      int itemCount = orderAddDTO.getItems() == null ? 0 : orderAddDTO.getItems().size();

      // 🚨 대시(-) 포맷을 명확하게 인식할 수 있도록 안전한 포맷터 정의
      java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

      OrderDTO orderDTO = OrderDTO.builder()
          .partnerId(orderAddDTO.getCustomerCompanyId())
          .itemCount(itemCount)
          // ⭕ 단순 parse 대신 포맷터를 지정하여 문자열을 안전하게 LocalDate로 변환합니다.
          .orderDate(LocalDate.parse(orderAddDTO.getOrderDate(), formatter))
          .deadline(LocalDate.parse(orderAddDTO.getDeadline(), formatter))
          .eta(LocalDate.parse(orderAddDTO.getEta(), formatter))
          .build();

      orderDao.add(orderDTO);

      if (orderDTO.getOrderId() > 0) { // 생성된 PK는 인자로 보낸 orderDTO에 채워져 있습니다.
        isSuccess = true;
        message = "Order 등록이 완료되었습니다.";
        if (itemCount > 0) {
          int size = 0;
          for (OrderDetailProductDTO item : orderAddDTO.getItems()) {
            // ⭕ OrderDetailProductDTO.builder()로 정상 변경
            OrderDetailProductDTO orderDetailProductDTO = OrderDetailProductDTO.builder()
                .orderId(orderDTO.getOrderId())
                .itemNo(item.getItemNo()) // 💡 Swagger로 보낸 itemNo(1, 2)가 여기 정상 매핑되도록 추가
                .itemName(item.getItemName())
                .setCount(item.getSetCount())
                .price(item.getPrice())
                .build();

            // Mapper로 수정된 DTO 객체를 안전하게 전달
            size += orderDao.addOrderProduct(orderDetailProductDTO);
          }

          if (size != itemCount) {
            isSuccess = false;
            message = "Order 등록이 일부 실패했습니다.";
          }
        }
      }

    } catch (Exception e) {
      log.info("OrderServiceImp add error : {}", e.getMessage());
      message = "Order 등록이 실패했습니다.";
    }
    return ResponseDTO.builder()
        .status(isSuccess)
        .data(request)
        .message(message)
        .build();
  }

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
      message = "주문(Order) 정보 조회가 완료되었습니다.";
    } catch (Exception e) {
      log.info("OrderServiceImp findAllOrders error : {}", e.getMessage());
      message = "주문(Order) 정보 조회가 실패했습니다.";
    }
    return ResponseDTO.builder()
        .status(isSuccess)
        .data(request)
        .message(message)
        .build();
  }

}
