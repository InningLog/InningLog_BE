package com.inninglog.inninglog.member.controller;

import com.inninglog.inninglog.global.auth.CustomUserDetails;
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
    public ResponseEntity<Void> updateNickname(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody NicknameRequestDto request
    ) {
        memberService.updateNickname(user.getMember().getId(), request.getNickname());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "회원 타입 및 팀 설정",
            description = "회원 ID를 바탕으로, 회원 타입(뉴비/고인물)과 응원 팀의 식별자(shortCode)를 설정합니다.\n" +
                    "회원 타입은 뉴비,NEWBIE 또는 고인물,VETERAN, 팀 shortCode는 DOOSAN, KIA 등으로 전달해야 합니다."
    )    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "404", description = "회원 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "이미 유저 타입이나 팀이 설정되어 있는 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/setup")
    public ResponseEntity<Void> updateType(
            @AuthenticationPrincipal CustomUserDetails user,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "유저 타입(뉴비/고인물)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TypeRequestDto.class)))
            @RequestBody TypeRequestDto request)
    {
        memberService.updateMemberType(user.getMember().getId(), request.getMemberType(), request.getTeamShortCode());
        return ResponseEntity.ok().build();
    }
}