package com.inninglog.inninglog.domain.journal.service;

import com.inninglog.inninglog.domain.journal.domain.Journal;
import com.inninglog.inninglog.domain.journal.repository.JournalRepository;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JournalGetService {

    private final JournalRepository journalRepository;

    @Transactional(readOnly = true)
    public Journal getJournalById(Long id){
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("⚠️ 존재하지 않는 일지: journalId={}", id);
                    return new CustomException(ErrorCode.JOURNAL_NOT_FOUND);
                });
        return journal;
    }

    @Transactional(readOnly = true)
    public Slice<Journal> getPublicJournals(Pageable pageable) {
        return journalRepository.findPublicJournals(pageable);
    }

    @Transactional(readOnly = true)
    public Slice<Journal> getPublicJournalsByTeam(String teamShortCode, Pageable pageable) {
        return journalRepository.findPublicJournalsByTeam(teamShortCode, pageable);
    }
}
