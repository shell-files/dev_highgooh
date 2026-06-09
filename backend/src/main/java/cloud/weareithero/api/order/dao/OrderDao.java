package cloud.weareithero.api.order.dao;

import java.util.List;

import cloud.weareithero.api.order.dto.OrderCustomerDTO;
import cloud.weareithero.api.order.dto.OrderDTO;
import cloud.weareithero.api.order.dto.OrderProductDTO;
import cloud.weareithero.api.order.dto.OrderDetailProductDTO;
import cloud.weareithero.api.order.dto.OrderRequestDTO;
import cloud.weareithero.api.order.dto.OrderSummaryDTO;

public interface OrderDao {

  public OrderSummaryDTO findSummary(OrderRequestDTO orderRequestDTO);

  public List<OrderDTO> findAll(OrderRequestDTO orderRequestDTO);

  public OrderDTO findByOrderId(int orderId);

  public List<OrderDetailProductDTO> findOne(int orderId);

  public int add(OrderDTO orderDTO);

  public int addOrderProduct(OrderDetailProductDTO orderDetailProductDTO);

  public List<OrderCustomerDTO> findByCustomer();

  public List<OrderProductDTO> findByProduct();

}
