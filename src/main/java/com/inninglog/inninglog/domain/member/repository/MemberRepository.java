package com.inninglog.inninglog.domain.member.repository;

import com.inninglog.inninglog.domain.member.domain.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByKakaoId(Long kakaoId);
    Boolean existsByNickname(String nickname);
}