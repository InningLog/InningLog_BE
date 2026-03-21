package com.inninglog.inninglog.domain.member.service;

import com.inninglog.inninglog.domain.kakao.service.KakaoService;
import com.inninglog.inninglog.domain.like.repository.LikeRepository;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.repository.MemberRepository;
import com.inninglog.inninglog.domain.scrap.repository.ScrapRepository;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberWithdrawService {

    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;
    private final ScrapRepository scrapRepository;
    private final KakaoService kakaoService;

    @Transactional
    public void withdraw(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (member.isDeleted()) {
            throw new CustomException(ErrorCode.ALREADY_DELETED_MEMBER);
        }

        kakaoService.unlinkKakaoUser(member.getKakaoId());

        likeRepository.deleteByMember(member);
        scrapRepository.deleteByMember(member);

        member.softDelete();
        member.setKakaoId(null);

        log.info("📌 [withdraw] memberId={} 회원 탈퇴 완료", memberId);
    }
}
