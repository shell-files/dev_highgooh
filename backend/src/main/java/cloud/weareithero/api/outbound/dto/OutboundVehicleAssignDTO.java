package cloud.weareithero.api.outbound.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "차량 배정 요청 DTO")
public class OutboundVehicleAssignDTO {

    @Schema(description = "배정 대상 출고 ID 목록 (체크박스 선택)")
    private List<Integer> outboundIds;

    @Schema(description = "운송사 ID (PARTNER_COMPANY_MASTER.id - carrier)")
    private int carrierId;

    @Schema(description = "차량 ID (TRANSPORTATION_VEHICLE_MASTER.id)")
    private int vehicleId;

    @Schema(description = "기사명")
    private String driver;

    // ※ addTransportation() 후 @Options(useGeneratedKeys)로 PK 자동 주입되는 필드
    // DaoImp.addTransportation() 에서 이 값을 읽어 반환합니다.
    private int transportationId;

}