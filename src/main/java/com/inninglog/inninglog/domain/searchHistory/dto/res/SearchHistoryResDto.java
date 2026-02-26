package com.inninglog.inninglog.domain.searchHistory.dto.res;

import com.inninglog.inninglog.domain.searchHistory.domain.SearchHistory;
import java.time.LocalDateTime;

public record SearchHistoryResDto(
        Long id,
        String keyword,
        LocalDateTime createdAt
) {
    public static SearchHistoryResDto from(SearchHistory searchHistory) {
        return new SearchHistoryResDto(
                searchHistory.getId(),
                searchHistory.getKeyword(),
                searchHistory.getCreatedAt()
        );
    }
}
