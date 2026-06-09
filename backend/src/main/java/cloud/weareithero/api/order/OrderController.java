package cloud.weareithero.api.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.api.order.dto.OrderAddDTO;
import cloud.weareithero.api.order.dto.OrderRequestDTO;
import cloud.weareithero.api.order.service.OrderService;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController implements OrderControllerDocs {

  private final OrderService OrderService;
  
  @PostMapping
  public ResponseDTO findAll(@RequestBody OrderRequestDTO orderRequestDTO) {
    return OrderService.findAll(orderRequestDTO);
  }

  @PostMapping("/{orderId:[0-9]+}")
  public ResponseDTO findOne(@PathVariable Integer orderId) {
    return OrderService.findOne(orderId);
  }

  @PutMapping
  public ResponseDTO add(@RequestBody OrderAddDTO orderAddDTO) {
    return OrderService.add(orderAddDTO);
  }

  @GetMapping
  public ResponseDTO findAllOrders() {
    return OrderService.findAllOrders();
  }

}
