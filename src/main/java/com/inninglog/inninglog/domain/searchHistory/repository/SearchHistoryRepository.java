package com.inninglog.inninglog.domain.searchHistory.repository;

import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.searchHistory.domain.SearchHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @Query("SELECT sh FROM SearchHistory sh WHERE sh.member = :member ORDER BY sh.createdAt DESC LIMIT 12")
    List<SearchHistory> findRecentByMember(@Param("member") Member member);

    Optional<SearchHistory> findByMemberAndKeyword(Member member, String keyword);

    void deleteByIdAndMember(Long id, Member member);
}
