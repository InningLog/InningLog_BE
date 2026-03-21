package com.inninglog.inninglog.domain.member.controller;

import com.inninglog.inninglog.domain.member.service.MemberWithdrawService;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "회원", description = "회원 관련 API")
public class MemberDeleteController {

    private final MemberWithdrawService memberWithdrawService;

    @Operation(
            summary = "회원 탈퇴",
            description = "카카오 연동을 해제하고 회원을 탈퇴 처리합니다. 작성한 게시글/댓글은 '알 수 없는 사용자'로 표시됩니다."
    )
    @DeleteMapping("/me")
    public ResponseEntity<SuccessResponse<Void>> withdraw(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        memberWithdrawService.withdraw(user.getMemberId());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.WITHDRAW_SUCCESS));
    }
}
