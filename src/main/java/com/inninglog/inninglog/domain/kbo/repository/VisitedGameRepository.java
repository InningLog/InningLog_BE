package com.inninglog.inninglog.domain.kbo.repository;

import com.inninglog.inninglog.domain.kbo.domain.VisitedGame;
import com.inninglog.inninglog.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitedGameRepository extends JpaRepository<VisitedGame, Long> {

    List<VisitedGame> findByMember(Member member);
}
