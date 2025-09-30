package com.inninglog.inninglog.domain.member.service;

import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.repository.MemberRepository;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberFindService {

    private final MemberRepository memberRepository;

    public Member findById(Long id) {
       return memberRepository.findById(id).
        orElseThrow(() ->{return new CustomException(ErrorCode.USER_NOT_FOUND);});
    }
}
