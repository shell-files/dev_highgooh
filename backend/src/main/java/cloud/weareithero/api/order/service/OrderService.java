package cloud.weareithero.api.order.service;

import cloud.weareithero.api.order.dto.OrderAddDTO;
import cloud.weareithero.api.order.dto.OrderRequestDTO;
import cloud.weareithero.api.order.dto.OrderUpdateDTO;
import cloud.weareithero.dto.ResponseDTO;

/**
 * Order Service 인터페이스
 * [DB 변경] OUTBOUND 테이블 기반으로 전면 재설계
 * update / delete 메서드 신규 추가 (Inbound에 없던 기능)
 */
public interface OrderService {

  public ResponseDTO findAll(OrderRequestDTO orderRequestDTO);
  public ResponseDTO findOne(int outboundId);
  public ResponseDTO add(OrderAddDTO orderAddDTO);
  public ResponseDTO update(int outboundId, OrderUpdateDTO orderUpdateDTO);
  // public ResponseDTO delete(int outboundId);
  public ResponseDTO findAllOrders();

}