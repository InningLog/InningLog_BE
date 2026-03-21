package com.inninglog.inninglog.domain.member.controller;

import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.service.MemberWithdrawService;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberWithdrawControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberWithdrawService memberWithdrawService;

    @Test
    @DisplayName("회원 탈퇴 성공")
    void withdraw_success() throws Exception {
        // given
        Member mockMember = Member.builder()
                .id(1L)
                .nickname("테스터")
                .kakaoId(12345L)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(mockMember, mockMember.getId());

        doNothing().when(memberWithdrawService).withdraw(eq(1L));

        // when & then
        mockMvc.perform(delete("/member/me")
                        .with(csrf())
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("WITHDRAW_SUCCESS"));

        verify(memberWithdrawService, times(1)).withdraw(eq(1L));
    }

    @Test
    @DisplayName("이미 탈퇴한 회원 - 400 에러")
    void withdraw_alreadyDeleted() throws Exception {
        // given
        Member mockMember = Member.builder()
                .id(1L)
                .nickname("테스터")
                .kakaoId(12345L)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(mockMember, mockMember.getId());

        doThrow(new CustomException(ErrorCode.ALREADY_DELETED_MEMBER))
                .when(memberWithdrawService).withdraw(eq(1L));

        // when & then
        mockMvc.perform(delete("/member/me")
                        .with(csrf())
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(memberWithdrawService, times(1)).withdraw(eq(1L));
    }
}
