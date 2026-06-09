package cloud.weareithero.api.inbound.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ASN 등록 DTO")
public class AsnAddDTO {

  @NotBlank(message = "협력사 ID를 입력하세요.")
  private int partnerCompanyId;

  @NotBlank(message = "창고 ID를 입력하세요.")
  private int warehouseId;

  @NotBlank(message = "예정 날짜를 입력하세요.")
  private String eta;
  
  private List<AsnOrderMaterialDTO> items;
  
}
