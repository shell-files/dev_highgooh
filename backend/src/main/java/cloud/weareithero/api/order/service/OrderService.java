package cloud.weareithero.api.order.service;

import cloud.weareithero.api.order.dto.OrderAddDTO;
import cloud.weareithero.api.order.dto.OrderRequestDTO;
import cloud.weareithero.dto.ResponseDTO;

public interface OrderService {
  
  public ResponseDTO findAll(OrderRequestDTO orderRequestDTO);
  public ResponseDTO findOne(int orderId);
  public ResponseDTO add(OrderAddDTO orderAddDTO);
  public ResponseDTO findAllOrders();

}
