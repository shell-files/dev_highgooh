package cloud.weareithero.api.outbound.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 운송사 목록 DTO (차량 배정 모달 드롭다운)
 * PARTNER_COMPANY_MASTER.carrier_yn_code = 1 인 운송사만 조회
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboundCarrierDTO {

    private int id;
    private String name;

}