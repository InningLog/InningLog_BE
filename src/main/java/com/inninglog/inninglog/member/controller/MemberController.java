package com.inninglog.inninglog.member.controller;

import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.response.CustomApiResponse;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.member.service.MemberService;
import com.inninglog.inninglog.member.dto.NicknameRequestDto;
import com.inninglog.inninglog.member.dto.TypeRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "Member", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;


    //닉네임 수정
    @Operation(summary = "닉네임 수정", description = "토큰에서 회원 정보를 받아 닉네임을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 수정 성공"),
            @ApiResponse(responseCode = "404", description = "회원 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/nickname")
    public ResponseEntity<CustomApiResponse<Void>> updateNickname(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody NicknameRequestDto request
    ) {
        memberService.updateNickname(user.getMember().getId(), request.getNickname());
        return ResponseEntity.ok(CustomApiResponse.success(SuccessCode.NICKNAME_UPDATED));
    }


    //회원 응원팀 설정
    @Operation(
            summary = "회원의 응원 팀 설정",
            description = """
        로그인한 회원의 응원 팀을 설정합니다.  
        요청 시 아래의 **구단 식별자(shortCode)** 중 하나를 전달해야 합니다.

        - LG 트윈스: `LG`
        - 두산 베어스: `OB`
        - SSG 랜더스: `SK`
        - 한화 이글스: `HH`
        - 삼성 라이온즈: `SS`
        - KT 위즈: `KT`
        - 롯데 자이언츠: `LT`
        - KIA 타이거즈: `HT`
        - NC 다이노스: `NC`
        - 키움 히어로즈: `WO`

        ⚠️ **주의:** 이미 응원 팀이 설정된 회원은 변경할 수 없습니다.  
        이 경우 `400 Bad Request` 에러가 반환됩니다.
    """
    )    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "404", description = "회원 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "이미 팀이 설정되어 있는 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/setup")
    public ResponseEntity<CustomApiResponse<Void>> updateType(
            @AuthenticationPrincipal CustomUserDetails user,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "유저가 응원하는 팀 설정",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TypeRequestDto.class)))
            @RequestBody TypeRequestDto request
    ) {
        memberService.updateMemberType(user.getMember().getId(), request.getTeamShortCode());
        return ResponseEntity.ok(CustomApiResponse.success(SuccessCode.TEAM_SET));
    }
}