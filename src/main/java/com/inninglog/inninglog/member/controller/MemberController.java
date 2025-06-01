package com.inninglog.inninglog.member.controller;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "Member", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;


    //닉네임 수정
    @Operation(summary = "닉네임 수정", description = "회원 ID와 닉네임을 받아 닉네임을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 수정 성공"),
            @ApiResponse(responseCode = "404", description = "회원 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}/nickname")
    public ResponseEntity<Void> updateNickname(
            @Parameter(description = "회원 ID", example = "1")
            @PathVariable("id") Long memberId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 닉네임 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = NicknameRequestDto.class)))
            @RequestBody NicknameRequestDto request
    ) {
        memberService.updateNickname(memberId, request.getNickname());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 타입 및 팀 설정", description = "회원 ID와 회원타입(뉴비/고인물), 응원팀 ID를 받아 회원 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "404", description = "회원 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "이미 유저 타입이나 팀이 설정되어 있는 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}/setup")
    public ResponseEntity<Void> updateType(
            @Parameter(description = "회원 ID", example = "1")
            @PathVariable("id") Long memberId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "유저 타입(NEWBIE/VETERAN)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TypeRequestDto.class)))
            @RequestBody TypeRequestDto request)
    {
        memberService.updateMemberType(memberId, request.getMemberType(), request.getTeam());

        return ResponseEntity.ok().build();
    }
}