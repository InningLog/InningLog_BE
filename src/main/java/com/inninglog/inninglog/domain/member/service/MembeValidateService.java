package com.inninglog.inninglog.domain.member.service;

import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.repository.MemberRepository;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembeValidateService {

    private final MemberRepository memberRepository;

    //유저 검증
    public Member findById(Long id) {
       return memberRepository.findById(id).
        orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    //유저의 팀 검증
    public void validateTeam(Member member) {
        if(member.getTeam() == null) {
            log.error("📌❌ 유저의 응원팀이 설정되지 않음: memberId={}", member.getId());
            throw new CustomException(ErrorCode.TEAM_NOT_FOUND);
        }
    }
}
