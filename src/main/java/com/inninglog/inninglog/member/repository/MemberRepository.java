package com.inninglog.inninglog.member.repository;

import com.inninglog.inninglog.member.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByKakaoId(Long kakaoId);
}