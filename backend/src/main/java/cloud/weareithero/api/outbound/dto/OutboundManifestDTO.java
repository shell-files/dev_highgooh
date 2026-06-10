package cloud.weareithero.api.outbound.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 매니페스트 뷰 목록 1행
 * 대상 테이블: OUTBOUND_TRANSPORTATION 기준 집계
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboundManifestDTO {

    private int transportationId;         // OUTBOUND_TRANSPORTATION.id

    // ※ 매니페스트 번호 채번 방식 DB에서 끌고 오는걸로 (단순 숫자)
    private String manifestNo;            // 매니페스트 번호

    private String carrierName;           // 운송사명
    private String vehicleInfo;           // 차량번호 + 종류 조합 문자열 (예: "11가 1234 (1톤 탑차)")
    private int boxCount;                 // 적재 박스 수 (COUNT 집계)

    private int stateCode;                // OUTBOUND_TRANSPORTATION.state_code 컬럼 없어서 추가함 // 수정
    private String stateName;             // COMMON_CODE에 outbound 부분 추가

    private LocalDateTime expectedDepartureAt; // OUTBOUND.etd 출발 예정 일자 (시간 단위로 관리 안하고 날짜 단위로 관리)
    private LocalDateTime deadline;            // OUTBOUND.deadline 에서 가져오기

}