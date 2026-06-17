package cloud.weareithero.api.outbound.dto;

import cloud.weareithero.dto.PageRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "출고 목록 조회 요청 데이터")
public class OutboundRequestDTO extends PageRequestDTO {

    @Schema(description = "출고 ID (OUTBOUND.id, 0이면 전체)", defaultValue = "0", example = "0")
    private int outboundId;

    @Schema(description = "고객사명 검색어 (PARTNER_COMPANY_MASTER.name LIKE)", defaultValue = "", example = "")
    private String customerName;

    @Schema(description = "진행 상태 코드 (OUTBOUND.state_code, 0이면 전체)", defaultValue = "0", example = "0")
    private int stateCode;

    @Schema(description = "주문 시작일 (OUTBOUND.order_date 기준)", defaultValue = "", example = "")
    private String orderStart;

    @Schema(description = "주문 종료일 (OUTBOUND.order_date 기준)", defaultValue = "", example = "")
    private String orderEnd;

    @Schema(description = "고객사 ID (PARTNER_COMPANY_MASTER.id, 0이면 전체)", defaultValue = "0", example = "0")
    private int clientId;
    
    @Schema(description = "운송사 ID (PARTNER_COMPANY_MASTER.id, 0이면 전체)", defaultValue = "0", example = "0")
    private int carrierId;

    @Schema(description = "박스 ID (OUTBOUND_PACKING.id, 0이면 전체)", defaultValue = "0", example = "0")
    private int packingId;

    @Schema(description = "매니페스트 ID (OUTBOUND_TRANSPORTATION.id, 0이면 전체)", defaultValue = "0", example = "0")
    private int transportationId;

    @Schema(description = "박스 패킹 번호 (OUTBOUND_PACKING.packing_invoice_number, ''이면 전체)", defaultValue = "", example = "")
    private String packingInvoiceNumber;
}