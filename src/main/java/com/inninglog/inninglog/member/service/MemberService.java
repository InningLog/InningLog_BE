package com.inninglog.inninglog.member.service;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.kakao.KakaoUserInfoResponseDto;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.member.dto.MemberWithFlag;
import com.inninglog.inninglog.member.repository.MemberRepository;
import com.inninglog.inninglog.team.domain.Team;
import com.inninglog.inninglog.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public MemberWithFlag saveOrUpdateMember(KakaoUserInfoResponseDto userInfo) {
        Optional<Member> existing = memberRepository.findByKakaoId(userInfo.getId());

        if (existing.isPresent()) {
            Member member = existing.get();
            member.updateInfo(userInfo);
            return new MemberWithFlag(member, false);
        } else {
            Member newMember = Member.fromKakaoDto(userInfo); // 💡 static 팩토리 메서드 사용
            memberRepository.save(newMember);
            return new MemberWithFlag(newMember, true);
        }
    }

    //닉네임 업데이트
    @Transactional
    public void updateNickname(Long memberId, String nickname) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 중복 닉네임 검사
        if (memberRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        member.setNickname(nickname);
    }


    //유저 타입 & 응원 팀 설정
    @Transactional
    public void updateMemberType(Long memberId, String teamShortCode) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (member.getTeam() != null) {
            throw new CustomException(ErrorCode.ALREADY_SET);
        }

        Team team = teamRepository.findByShortCode(teamShortCode)
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        member.setTeam(team);
    }
}
