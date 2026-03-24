package com.inninglog.inninglog.domain.seatView.service;

import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.repository.MemberRepository;
import com.inninglog.inninglog.domain.seatView.domain.RecentSeatSearch;
import com.inninglog.inninglog.domain.seatView.dto.res.RecentSeatSearchRes;
import com.inninglog.inninglog.domain.seatView.repository.RecentSeatSearchRepository;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecentSeatSearchService {

    private static final int MAX_RECENT_SEARCHES = 3;

    private final RecentSeatSearchRepository recentSeatSearchRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveRecentSearch(Long memberId, String stadiumShortCode, String section, String seatRow) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Optional<RecentSeatSearch> existing = recentSeatSearchRepository
                .findByMemberAndStadiumShortCodeAndSectionAndSeatRow(member, stadiumShortCode, section, seatRow);

        if (existing.isPresent()) {
            existing.get().updateSearchedAt();
            log.info("🔄 [saveRecentSearch] memberId={}, stadium={}, section={}, row={} 기존 검색 기록 갱신",
                    memberId, stadiumShortCode, section, seatRow);
            return;
        }

        if (recentSeatSearchRepository.countByMemberAndStadiumShortCode(member, stadiumShortCode) >= MAX_RECENT_SEARCHES) {
            RecentSeatSearch oldest = recentSeatSearchRepository
                    .findFirstByMemberAndStadiumShortCodeOrderBySearchedAtAsc(member, stadiumShortCode)
                    .orElseThrow();
            recentSeatSearchRepository.delete(oldest);
            log.info("🗑️ [saveRecentSearch] memberId={}, 가장 오래된 검색 기록 삭제 id={}", memberId, oldest.getId());
        }

        RecentSeatSearch newSearch = RecentSeatSearch.builder()
                .member(member)
                .stadiumShortCode(stadiumShortCode)
                .section(section)
                .seatRow(seatRow)
                .searchedAt(LocalDateTime.now())
                .build();

        recentSeatSearchRepository.save(newSearch);
        log.info("✅ [saveRecentSearch] memberId={}, stadium={}, section={}, row={} 새 검색 기록 저장",
                memberId, stadiumShortCode, section, seatRow);
    }

    @Transactional(readOnly = true)
    public List<RecentSeatSearchRes> getRecentSearches(Long memberId, String stadiumShortCode) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return recentSeatSearchRepository
                .findByMemberAndStadiumShortCodeOrderBySearchedAtDesc(member, stadiumShortCode)
                .stream()
                .map(RecentSeatSearchRes::from)
                .toList();
    }
}
