package cloud.weareithero.inbound.api.asn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsnCompleteDTO {
    private int asnId;
    private String ata;
}
