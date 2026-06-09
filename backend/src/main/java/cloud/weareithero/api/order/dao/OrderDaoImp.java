package cloud.weareithero.api.order.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cloud.weareithero.api.order.dao.OrderDao;
import cloud.weareithero.api.order.dao.OrderMapper;
import cloud.weareithero.api.order.dto.OrderDTO;
import cloud.weareithero.api.order.dto.OrderProductDTO;
import cloud.weareithero.api.order.dto.OrderDetailProductDTO;
import cloud.weareithero.api.order.dto.OrderRequestDTO;
import cloud.weareithero.api.order.dto.OrderSummaryDTO;
import cloud.weareithero.api.order.dto.OrderCustomerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderDaoImp implements OrderDao {

  private final OrderMapper orderMapper;

  @Override
  public OrderSummaryDTO findSummary(OrderRequestDTO orderRequestDTO) {
    return orderMapper.findSummary(orderRequestDTO);
  }

  @Override
  public List<OrderDTO> findAll(OrderRequestDTO orderRequestDTO) {
    return orderMapper.findAll(orderRequestDTO);
  }

  @Override
  public OrderDTO findByOrderId(int orderId) {
    return orderMapper.findByOrderId(orderId);
  }

  @Override
  public List<OrderDetailProductDTO> findOne(int orderId) {
    return orderMapper.findOne(orderId);
  }

  @Override
  public int add(OrderDTO orderDTO) {
    return orderMapper.add(orderDTO);
  }

  @Override
  public int addOrderProduct(OrderDetailProductDTO orderDetailProductDTO) {
    return orderMapper.addOrderProduct(orderDetailProductDTO);
  }

  @Override
  public List<OrderCustomerDTO> findByCustomer() {
    return orderMapper.findByCustomer();
  }

  @Override
  public List<OrderProductDTO> findByProduct() {
    return orderMapper.findByProduct();
  }
  
}
