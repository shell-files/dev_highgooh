package cloud.weareithero.api.outbound.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutboundDTO {

    private int outboundId;           // OUTBOUND.id
    private String orderNo;           // OUTBOUND.id가 주문번호의 역할을 해서 outboundId 둘 중 하나만 있어도 되지 않나?
    private String customerName;      // PARTNER_COMPANY_MASTER.name (고객사명)
    private String carrierName;       // 운송사명 (PARTNER_COMPANY_MASTER.carrier_yn_code)

    private String invoiceNo;         // 송장번호 (invoice_no 컬럼 존재)
    private LocalDateTime etd;        // 기한 / 예상 출고 일시 (OUTBOUND.etd)

    private int stateCode;            // OUTBOUND.state_code (COMMON_CODE 테이블에서 id 가져옴)
    private String stateName;         // COMMON_CODE.name (출고대기 / 출고처리중 / 출고완료 / 부분출고 총 4개 존재함)

    // 매니페스트 뷰 연결용 (차량 배정 후 설정)
    private Integer transportationId;     // OUTBOUND_TRANSPORTATION.id (FK: transportation_vehicle_id 컬럼 존재)

}