package cloud.weareithero.inbound.docs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cloud.weareithero.inbound.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
  @ApiResponse(
    responseCode = "400", 
    description = "클라이언트 요청 에러 모음",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = ResponseDTO.class),
      examples = {
        @ExampleObject(name = "인증 실패 예시", value = ResponseExamples.UNAUTHORIZED),
        @ExampleObject(name = "접근 권한 예시", value = ResponseExamples.ACCESS_DENIED),
        @ExampleObject(name = "사용자 없음 예시", value = ResponseExamples.USER_NOT_FOUND)
      }
    )
  ),
  @ApiResponse(
    responseCode = "500", 
    description = "서버 내부 오류",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = ResponseDTO.class),
      examples = @ExampleObject(name = "서버 오류 예시", value = "{\"status\":false,\"result\":null,\"message\":\"Internal Server Error\"}")
    )
  )
})
public @interface ApiCommonErrors {
  
}
