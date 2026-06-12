package cloud.weareithero.api.outbound.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 운송사 목록 DTO (차량 배정 모달 드롭다운)
 * PARTNER_COMPANY_MASTER WHERE carrier_yn_code = 1
 * Inbound의 AsnSupplierDTO(supplier_yn_code=1) 대칭 구조
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboundCarrierDTO {

    private int id;
    private String name;

}