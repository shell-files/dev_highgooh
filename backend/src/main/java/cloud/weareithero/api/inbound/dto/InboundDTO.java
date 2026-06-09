package cloud.weareithero.api.inbound.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InboundDTO {

    private LocalDate eta; // : "2026-06-02", // 입고예정일
    private int asnId; // : "12", // ASN 번호
    private int partnerId; // : "1", // 공급사 번호, 발주처(고객사)
    private String partnerName; // : "(주)한성자재마트", // 공급사명, 발주처(고객사)
    private int warehouseId; // : "1", // 입고 번호
    private String warehouseName; // : "제 1 자재창고", // 입고 창고

}
