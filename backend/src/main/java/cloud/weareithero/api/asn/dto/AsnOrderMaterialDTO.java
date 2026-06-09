package cloud.weareithero.api.asn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ASN 상세 정보 DTO")
public class AsnOrderMaterialDTO {

  private int no;
  private int inboundId;
  private int itemNo;
  private String itemName;
  private double diameter;
  private double weight;

}
