package cloud.weareithero.inbound.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
  
  @Builder.Default
  private int page = 1;
  @Builder.Default
  private int size = 20;
  public int getOffset() {
    return (this.page - 1) * this.size;
  }

}
