package cloud.weareithero.api.outbound.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 매니페스트 뷰 목록 1행
 *
 * 기준 테이블: OUTBOUND_TRANSPORTATION (3차안 기준)
 *   id, carrier_company_id, transportation_vehicle_id, lpn, driver,
 *   etd, atd, state_code, updated_at
 *
 * JOIN:
 *   PARTNER_COMPANY_MASTER carrier (carrier_company_id)
 *   TRANSPORTATION_VEHICLE_MASTER tvm (transportation_vehicle_id)
 *   OUTBOUND_PACKING op → COUNT (박스 수 집계)
 *   COMMON_CODE cc (OUTBOUND_TRANSPORTATION.state_code)
 *
 * ──────────────────────────────────────────────
 * JSX 매니페스트 테이블 컬럼 → DB 필드 매핑
 * ──────────────────────────────────────────────
 * mnfNo   → CONCAT('MNF-', LPAD(ot.id, 6, '0')) 가공 (별도 컬럼 없음)
 * carrier → PARTNER_COMPANY_MASTER.name
 * vehicle → CONCAT(ot.lpn, ' (', tvm.vehicle_type, ')')
 * boxes   → COUNT(op.id) || ' BOX'
 * status  → COMMON_CODE.name
 * due     → ot.etd (프론트에서 NOW()와 비교)
 *
 * ※ JSX 체크박스 활성 조건: status === '차량배정'
 *   → COMMON_CODE에서 '차량배정' state_code 정수값 확인 필요
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboundManifestDTO {

    private int transportationId;       // OUTBOUND_TRANSPORTATION.id
    private String manifestNo;          // CONCAT 가공 (MNF-000001 형식)
    private String carrierName;         // 운송사명
    private String vehicleInfo;         // 차량번호 + 종류 (lpn + vehicle_type)
    private int boxCount;               // 연결된 OUTBOUND_PACKING COUNT
    private int stateCode;              // OUTBOUND_TRANSPORTATION.state_code
    private String stateName;           // COMMON_CODE.name
    private LocalDateTime etd;          // OUTBOUND_TRANSPORTATION.etd (예상 출고일)
    private LocalDateTime atd;          // OUTBOUND_TRANSPORTATION.atd (실제 출고일)
    private String driver;              // OUTBOUND_TRANSPORTATION.driver

}