package cloud.weareithero.outbound.api.outboundHistory.dto;

import cloud.weareithero.outbound.dto.PageRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "출고 이력 동적 검색 요청 데이터")
public class OutboundHistoryRequestDTO extends PageRequestDTO {

  @Schema(description = "출고 ID (단건 검색용)", defaultValue = "0", example = "0")
  private Integer outboundId;

  @Schema(description = "검색 시작일 (updated_at 기준)", defaultValue = "2026-06-01", example = "2026-06-01")
  private String orderStart;

  @Schema(description = "검색 종료일 (updated_at 기준)", defaultValue = "2026-06-15", example = "2026-06-15")
  private String orderEnd;

  @Schema(description = "고객사 ID 필터", defaultValue = "0", example = "1")
  private Integer partnerCompanyId;

  @Schema(description = "운송사 ID 필터", defaultValue = "0", example = "2")
  private Integer transportCompanyId;

  // 💡 MyBatis의 #{skip} 바인딩을 위한 가상 Getter 추가
  // 부모인 PageRequestDTO의 getPage()와 getSize()를 활용해 계산합니다.
 public int getSkip() {
    // 부모 클래스의 getter 메서드를 호출하거나, 
    // 만약 Lombok getter가 getter 대신 필드 접근이 가능하다면 상속된 값을 사용합니다.
    int currentPage = (super.getPage() > 0) ? super.getPage() : 1;
    int currentSize = (super.getSize() > 0) ? super.getSize() : 10;
    
    return (currentPage - 1) * currentSize;
}
}