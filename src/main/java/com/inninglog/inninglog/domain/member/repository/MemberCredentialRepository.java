package com.inninglog.inninglog.domain.member.repository;

import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.domain.MemberCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberCredentialRepository extends JpaRepository<MemberCredential,Long> {
    boolean existsByUserID(String username);
    Optional<MemberCredential> findByUserID(String username);
    Optional<Member> findByMemberId(Long memberId);
}
