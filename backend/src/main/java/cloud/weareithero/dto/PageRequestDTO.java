package cloud.weareithero.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
  
  private int page = 1;
  private int size = 20;
  public int getOffset() {
    return (this.page - 1) * this.size;
  }

}
