package com.inninglog.inninglog.domain.searchHistory.service;

import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.searchHistory.domain.SearchHistory;
import com.inninglog.inninglog.domain.searchHistory.dto.res.SearchHistoryResDto;
import com.inninglog.inninglog.domain.searchHistory.repository.SearchHistoryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    @Transactional(readOnly = true)
    public List<SearchHistoryResDto> getRecentSearches(Member member) {
        return searchHistoryRepository.findRecentByMember(member).stream()
                .map(SearchHistoryResDto::from)
                .toList();
    }

    @Transactional
    public void saveSearchKeyword(Member member, String keyword) {
        // 동일 키워드가 있으면 삭제 후 재생성 (최신순 갱신)
        Optional<SearchHistory> existing = searchHistoryRepository.findByMemberAndKeyword(member, keyword);
        existing.ifPresent(searchHistoryRepository::delete);

        searchHistoryRepository.save(SearchHistory.of(keyword, member));
    }

    @Transactional
    public void deleteSearchHistory(Long id, Member member) {
        searchHistoryRepository.deleteByIdAndMember(id, member);
    }
}
