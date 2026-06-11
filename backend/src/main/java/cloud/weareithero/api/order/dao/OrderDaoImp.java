package cloud.weareithero.api.order.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cloud.weareithero.api.order.dto.OrderCustomerDTO;
import cloud.weareithero.api.order.dto.OrderDTO;
import cloud.weareithero.api.order.dto.OrderDetailProductDTO;
import cloud.weareithero.api.order.dto.OrderProductDTO;
import cloud.weareithero.api.order.dto.OrderRequestDTO;
import cloud.weareithero.api.order.dto.OrderSummaryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Order DaoImpl - Mapper 위임 레이어
 * Inbound AsnDaoImp 패턴 동일 유지
 */
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
  public OrderDTO findByOutboundId(int outboundId) {
    return orderMapper.findByOutboundId(outboundId);
  }

  @Override
  public List<OrderDetailProductDTO> findOne(int outboundId) {
    return orderMapper.findOne(outboundId);
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
  public int update(OrderDTO orderDTO) {
    return orderMapper.update(orderDTO);
  }

  // @Override
  // public int deleteOrderProducts(int outboundId) {
  //   return orderMapper.deleteOrderProducts(outboundId);
  // }

  // @Override
  // public int delete(int outboundId) {
  //   return orderMapper.delete(outboundId);
  // }

  @Override
  public List<OrderCustomerDTO> findByCustomer() {
    return orderMapper.findByCustomer();
  }

  @Override
  public List<OrderProductDTO> findByProduct() {
    return orderMapper.findByProduct();
  }

}