package cloud.weareithero.api.outbound.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 차량 목록 DTO (차량 배정 모달 드롭다운)
 * TRANSPORTATION_VEHICLE_MASTER 전체 조회
 *
 * DB 확인된 컬럼: id, vehicle_type, max_payload_ton, max_volume
 * ※ 차량 번호판(lpn)은 OUTBOUND_TRANSPORTATION에 저장, 마스터 테이블에 없음
 *    차량 배정 모달에서 lpn을 직접 입력받아 OutboundVehicleAssignDTO.lpn 으로 전달
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboundTransportationVehicleDTO {

    private int id;
    private String vehicleType;       // TRANSPORTATION_VEHICLE_MASTER.vehicle_type
    private double maxPayloadTon;     // TRANSPORTATION_VEHICLE_MASTER.max_payload_ton

}