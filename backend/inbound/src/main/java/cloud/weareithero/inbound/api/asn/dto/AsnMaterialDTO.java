package cloud.weareithero.inbound.api.asn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsnMaterialDTO {

  private int id;
  private String name;
  private String alloyType;

}
