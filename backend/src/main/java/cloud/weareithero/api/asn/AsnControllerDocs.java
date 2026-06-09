package cloud.weareithero.api.asn;

import cloud.weareithero.api.asn.dto.AsnAddDTO;
import cloud.weareithero.api.asn.dto.AsnRequestDTO;
import cloud.weareithero.docs.ApiCommonErrors;
import cloud.weareithero.docs.ApiCommonSuccess;
import cloud.weareithero.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "ASN 관리", description = "ASN 목록 조회 · ASN 생성 · ASN 상세 조회 API")
public interface AsnControllerDocs {

  @Operation(summary = "입고 내역 조회", description = "ASN API")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO findAll(
      @RequestBody(content = @Content(schema = @Schema(implementation = AsnRequestDTO.class), examples = {
          @ExampleObject(name = "1. 전체 검색 예시", value = AsnResponseExamples.SUCCESS1_DEFAULT, description = "전체 기간 동안의 입고 내역을 조회할 때 사용합니다."),
          @ExampleObject(name = "2. 입고 ID 단건 검색 예시", value = AsnResponseExamples.SUCCESS2_DEFAULT, description = "날짜 상관없이 특정 입고 번호로만 검색할 때 사용합니다."),
          @ExampleObject(name = "3. 날짜 범위 검색 예시", value = AsnResponseExamples.SUCCESS3_DEFAULT, description = "특정 기간 동안의 입고 내역을 조회할 때 사용합니다."),
          @ExampleObject(name = "4. 날짜 범위 및 입고 ID 검색 예시", value = AsnResponseExamples.SUCCESS4_DEFAULT, description = "특정 기간 동안의 해당 입고 Id 내역을 조회할 때 사용합니다."),
      })) AsnRequestDTO asnRequestDTO);

  @Operation(summary = "입고 상세 조회", description = "ASN API")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO findOne(Integer asnId);

  @Operation(summary = "입고 생성", description = "ASN API")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO add(AsnAddDTO asnAddDTO);

  @Operation(summary = "사전입고 통지(ASN) 정보 조회", description = "ASN API")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO findAllAsn();

}
