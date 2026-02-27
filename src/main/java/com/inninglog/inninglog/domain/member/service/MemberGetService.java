package com.inninglog.inninglog.domain.member.service;

import com.inninglog.inninglog.domain.journal.domain.ResultScore;
import com.inninglog.inninglog.domain.journal.repository.JournalRepository;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;
import com.inninglog.inninglog.domain.member.dto.res.MemberTeamResDto;
import com.inninglog.inninglog.domain.member.dto.res.MyPageProfileResDto;
import com.inninglog.inninglog.domain.member.repository.MemberRepository;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberGetService {
    private final MemberRepository memberRepository;
    private final JournalRepository journalRepository;

    @Transactional(readOnly = true)
    public MemberShortResDto toMemberShortResDto(Member member) {
        return MemberShortResDto.from(member);
    }

    @Transactional(readOnly = true)
    public MemberTeamResDto getMemberTeam(Long memberId) {
        Member member = memberRepository.findByIdWithTeam(memberId)
                .orElseThrow();

        return MemberTeamResDto.from(
                member.getTeam().getShortCode()
        );
    }

    @Transactional(readOnly = true)
    public Member getMemberWithTeam(Long memberId) {
        return memberRepository.findByIdWithTeam(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public MyPageProfileResDto getMyPageProfile(Long memberId) {
        Member member = memberRepository.findByIdWithTeam(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        long totalCount = journalRepository.countByMember(member);
        long winCount = journalRepository.countByMemberAndResultScore(member, ResultScore.WIN);

        return MyPageProfileResDto.from(member, totalCount, winCount);
    }
}
