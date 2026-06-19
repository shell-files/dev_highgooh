package cloud.weareithero.inbound.api.asn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsnSummaryDTO {

  private int total;
  private int expected;
  private int completed;

}
