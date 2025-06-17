package com.inninglog.inninglog.journal.repository;

import com.inninglog.inninglog.journal.domain.Journal;
import com.inninglog.inninglog.journal.domain.ResultScore;
import com.inninglog.inninglog.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
    List<Journal> findAllByMember(Member member);

    List<Journal> findAllByMemberAndResultScore(Member member, ResultScore resultScore);

    Page<Journal> findAllByMember(Member member, Pageable pageable);

    Page<Journal> findAllByMemberAndResultScore(Member member, ResultScore resultScore, Pageable pageable);
}
