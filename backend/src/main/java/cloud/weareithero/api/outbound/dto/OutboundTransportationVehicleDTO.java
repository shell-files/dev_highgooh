package cloud.weareithero.api.outbound.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 차량 목록 DTO (차량 배정 모달 드롭다운)
 * Inbound의 AsnWarehouseDTO와 대응하는 구조
 * 대상 테이블: TRANSPORTATION_VEHICLE_MASTER
 *
 * ERD 확인된 컬럼: id, vehicle_type, max_payload_ton, max_volume
 * ※ vehicle_number 컬럼명 ERD 확인 필요 (화면에서 "서울88바 1234" 형식 표시됨)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboundTransportationVehicleDTO {

    private int id;
    private String vehicleType;       // TRANSPORTATION_VEHICLE_MASTER.vehicle_type
    private String vehicleNumber;     // 이게 정확히 뭘 알고 싶은거지? 차 몇 대 있는지?
    private double maxPayloadTon;     // TRANSPORTATION_VEHICLE_MASTER.max_payload_ton

}