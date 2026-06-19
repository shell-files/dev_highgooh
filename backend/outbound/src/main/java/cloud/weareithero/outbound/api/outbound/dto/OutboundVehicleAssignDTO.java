package cloud.weareithero.outbound.api.outbound.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 차량 배정 요청 DTO
 *
 * 박스 탭에서 체크박스로 선택한 OUTBOUND_PACKING.id 목록을 받아
 * OUTBOUND_TRANSPORTATION 1건 INSERT 후 각 OUTBOUND_PACKING 에 연결
 *
 * ※ JSX 차량 배정 모달 입력 항목:
 *   - 운송사 (select)
 *   - 차량 (select)
 *   - 예상 출발 일시 (datetime-local, 주석처리됨 - 필요 시 활성화)
 *
 * ※ 기사명(driver), 차량번호(lpn)는 차량 배정 시 직접 입력
 *   → OUTBOUND_TRANSPORTATION.driver, lpn 에 저장
 */
@Setter @Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "차량 배정 요청 DTO")
public class OutboundVehicleAssignDTO {

    @Schema(description = "배정 대상 OUTBOUND_PACKING.id 목록 (체크박스 선택)")
    private List<Integer> packingIds;

    @Schema(description = "운송사 ID (PARTNER_COMPANY_MASTER.id, carrier_yn_code=1)")
    private int carrierId;

    @Schema(description = "차량 ID (TRANSPORTATION_VEHICLE_MASTER.id)")
    private int vehicleId;

    @Schema(description = "차량 번호판 (OUTBOUND_TRANSPORTATION.lpn)")
    private String lpn;

    @Schema(description = "기사명 (OUTBOUND_TRANSPORTATION.driver)")
    private String driver;

    @Schema(description = "예상 출고 일자 (OUTBOUND_TRANSPORTATION.etd, 'yyyy-MM-dd' 형식)")
    private String etd;

    // addTransportation() 후 @Options(useGeneratedKeys)로 PK 자동 주입
    // DaoImp에서 이 값을 읽어 반환
    private int transportationId;

}