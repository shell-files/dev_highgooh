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

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
  responseCode = "200", 
  description = "홈페이지 데이터 조회 성공",
  content = @Content(
    mediaType = "application/json",
    schema = @Schema(implementation = ResponseDTO.class),
    examples = {
      @ExampleObject(
        name = "조회 성공 예시",
        summary = "일반적인 결과 응답",
        value = ResponseExamples.SUCCESS_DEFAULT
      )
    }
  )
)
public @interface ApiCommonSuccess {
  
}
