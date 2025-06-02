package com.inninglog.inninglog.journal.repository;

import com.inninglog.inninglog.journal.domain.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
}
