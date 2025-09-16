package com.inninglog.inninglog.kbo.repository;

import com.inninglog.inninglog.kbo.domain.VisitedGame;
import com.inninglog.inninglog.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitedGameRepository extends JpaRepository<VisitedGame, Long> {

    List<VisitedGame> findByMember(Member member);
}
