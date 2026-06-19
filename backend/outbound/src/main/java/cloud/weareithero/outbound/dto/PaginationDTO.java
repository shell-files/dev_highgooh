package cloud.weareithero.outbound.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginationDTO {
  
  private int page;
  private int totalCount;
  private int totalPages;

}
