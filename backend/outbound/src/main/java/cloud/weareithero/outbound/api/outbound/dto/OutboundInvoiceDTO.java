package cloud.weareithero.outbound.api.outbound.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 송장 발급 요청 DTO
 *
 * OUTBOUND_PACKING.invoice_number 에 운송장번호를 저장하고
 * state_code를 변경하는 UPDATE 요청
 *
 * ※ packingIds: 복수 박스 일괄 발급 지원
 *   (JSX 송장 모달의 activeInvoiceBoxes 목록 기준)
 * ※ invoiceNumber: ServiceImp에서 채번 후 Mapper 호출 시 설정
 *   → 현재 단일 invoiceNumber를 루프 내 개별 재설정하는 구조
 *   → 박스별 고유 번호가 필요한 경우 추가 로직 필요
 *
 * [DB 컬럼 설명]
 * OUTBOUND_PACKING.invoice_number    = 운송장번호 (발급 후 저장)
 * OUTBOUND_PACKING.packing_invoice_number = 패킹 송장번호 (박스 식별, 기존 값 유지)
 */
@Setter @Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "송장 발급 요청 DTO")
public class OutboundInvoiceDTO {

    @Schema(description = "송장 발급 대상 OUTBOUND_PACKING.id 목록")
    private List<Integer> packingIds;

    // ServiceImp 루프 내에서 개별 packingId 설정용 (단건 UPDATE 시 사용)
    private int packingId;

    @Schema(description = "운송장번호 (OUTBOUND_PACKING.invoice_number, 채번 후 저장)")
    private String invoiceNumber; 

    // TODO: 송장번호 채번 방식 팀 결정 필요
    //   옵션 A) 날짜 + OUTBOUND_PACKING.id 조합 (INV-YYYYMMDD-{packingId})

}