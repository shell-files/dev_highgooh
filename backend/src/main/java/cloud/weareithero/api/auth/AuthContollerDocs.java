package cloud.weareithero.api.auth;

import cloud.weareithero.api.auth.dto.UserDTO;
import cloud.weareithero.docs.ApiCommonErrors;
import cloud.weareithero.docs.ApiCommonSuccess;
import cloud.weareithero.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "사용자관리", description = "로그인 · 사용자 정보 조회 · 로그아웃 API")
public interface AuthContollerDocs {
  
  @Operation(summary = "사용자 정보 조회", description = "Auth API")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO getAuth();

  @Operation(summary = "사용자 로그인", description = "Auth API")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO postAuth(UserDTO userDTO, HttpServletResponse response, HttpServletRequest request);

  @Operation(summary = "사용자 로그아웃", description = "Auth API")
  @ApiCommonSuccess
  @ApiCommonErrors
  public ResponseDTO deleteAuth(HttpServletResponse response, HttpServletRequest request);

} 
