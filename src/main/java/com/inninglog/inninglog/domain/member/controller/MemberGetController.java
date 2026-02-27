package com.inninglog.inninglog.domain.member.controller;

import com.inninglog.inninglog.domain.member.dto.res.MemberTeamResDto;
import com.inninglog.inninglog.domain.member.dto.res.MyPageProfileResDto;
import com.inninglog.inninglog.domain.member.service.MemberGetService;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "회원", description = "회원 관련 API")
public class MemberGetController {

    private final MemberGetService memberGetService;

    @Operation(
            summary = "내 팀 정보 조회",
            description = """
                로그인한 사용자의 소속 팀 정보를 조회합니다.
                
                - 인증된 사용자만 접근 가능합니다.
                - 현재 로그인된 사용자의 팀 정보를 반환합니다.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "팀 정보 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 팀 정보를 찾을 수 없음"
            )
    })
    @GetMapping("/team")
    public ResponseEntity<SuccessResponse<MemberTeamResDto>> getMemberTeam(
            @AuthenticationPrincipal CustomUserDetails user
    ){
        MemberTeamResDto dto = memberGetService.getMemberTeam(user.getMemberId());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, dto));
    }

    @Operation(
            summary = "마이페이지 프로필 조회",
            description = """
                로그인한 사용자의 마이페이지 프로필 정보를 조회합니다.

                - 닉네임, 프로필 이미지, 응원팀 정보를 반환합니다.
                - 직관 통계(총 직관 수, 승리 수, 승률)를 포함합니다.
                """,
            tags = {"마이페이지"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음"
            )
    })
    @GetMapping("/profile")
    public ResponseEntity<SuccessResponse<MyPageProfileResDto>> getMyPageProfile(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        MyPageProfileResDto dto = memberGetService.getMyPageProfile(user.getMemberId());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, dto));
    }
}
