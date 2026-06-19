package cloud.weareithero.inbound.api.asn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsnWarehouseDTO {

  private int id;
  private String name;
  private int scale;
  private String type;

}
