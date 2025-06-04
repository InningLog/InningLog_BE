package com.inninglog.inninglog.journal.repository;

import com.inninglog.inninglog.journal.domain.Journal;
import com.inninglog.inninglog.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
    List<Journal> findAllByMember(Member member);
}
