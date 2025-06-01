package com.inninglog.inninglog.member;

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
}