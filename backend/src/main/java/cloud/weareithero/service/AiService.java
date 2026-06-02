package cloud.weareithero.service;

import cloud.weareithero.dto.OrderDTO;

public interface AiService {
  
  public OrderDTO chat(String rawText);

}
