package cloud.weareithero.inbound.api.asn.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsnDTO {

  private int asnId; // : "PO-20260602-X01", // ASN 번호
  private int partnerId; // : "1", // 공급사 번호, 발주처(고객사)
  private String partnerName; // : "(주)한성자재마트", // 공급사명, 발주처(고객사)
  private int warehouseId; // : "1", // 입고 번호
  private String warehouseName; // : "제 1 자재창고", // 입고 창고
  private String vehicleNumber; // : "경기00가1234", // 차량 번호
  private int itemCount; // : 2, // 품목 건수
  private LocalDate eta; // : "2026-06-02", // 입고예정일
  private String step; // : "NEW" // 진행상태
  private LocalDate orderDate; // : "2026-06-01" // 주문 일시

}
