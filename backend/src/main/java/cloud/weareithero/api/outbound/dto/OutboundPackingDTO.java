package cloud.weareithero.api.outbound.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 박스 뷰 목록 1행 DTO
 *
 * 기준 테이블: OUTBOUND_PACKING
 * id, outbound_id, packing_invoice_number, outbound_product_id,
 * partner_company_id(운송사), invoice_number, outbound_transportation_id,
 * state_code, updated_at
 *
 * JOIN:
 * OUTBOUND (outbound_id)
 * PARTNER_COMPANY_MASTER pcm (OUTBOUND.partner_company_id - 고객사)
 * PARTNER_COMPANY_MASTER carrier (OUTBOUND_PACKING.partner_company_id - 운송사,
 * LEFT JOIN)
 * OUTBOUND_TRANSPORTATION (outbound_transportation_id, LEFT JOIN)
 * COMMON_CODE (OUTBOUND_PACKING.state_code)
 *
 * ──────────────────────────────────
 * JSX 박스 테이블 컬럼 → DB 필드 매핑
 * ──────────────────────────────────
 * boxNo → packing_invoice_number (패킹 송장번호, 박스 식별자)
 * orderNo → outbound_id (OUTBOUND.id)
 * ※ 'PO-YYYYMMDD-' 형식은 DB에 없음 → 프론트 가공 또는 팀 협의 필요
 * customer → PARTNER_COMPANY_MASTER.name (고객사)
 * carrier → PARTNER_COMPANY_MASTER.name (운송사, carrier_yn_code=1)
 * status → COMMON_CODE.name (OUTBOUND_PACKING.state_code 기준)
 * invNo → invoice_number (운송장번호, 송장 발급 후 저장)
 * due → OUTBOUND.deadline (프론트에서 NOW()와 비교하여 '17분 남음' 등 표시)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutboundPackingDTO {

    private int packingId; // OUTBOUND_PACKING.id
    private String packingInvoiceNumber; // OUTBOUND_PACKING.packing_invoice_number (박스번호)
    private int outboundId; // OUTBOUND_PACKING.outbound_id (주문번호)
    private String customerName; // 고객사명 (OUTBOUND → PARTNER_COMPANY_MASTER)
    private String carrierName; // 운송사명 (OUTBOUND_PACKING.partner_company_id → PARTNER_COMPANY_MASTER)
    private String invoiceNumber; // OUTBOUND_PACKING.invoice_number (운송장번호, 발급 전 null)
    private int stateCode; // OUTBOUND_PACKING.state_code
    private String stateName; // COMMON_CODE.name
    private LocalDate deadline; // OUTBOUND.deadline (기한, 프론트 계산 기준)
    private LocalDate etd; // OUTBOUND.etd (출발 예정일)
    private Integer transportationId; // OUTBOUND_PACKING.outbound_transportation_id (null=미배정)

    @Schema(description = "운송사 ID (차량 배정 모달용)")
    private Integer carrierId; // LEFT JOIN이므로 Integer(nullable)
    // ※ selectable(체크박스 활성 여부): transportationId IS NULL 이면 true
    // 프론트에서 이 필드 기반으로 체크박스 disabled 처리 가능
    // 또는 별도 boolean 필드 추가 가능

}