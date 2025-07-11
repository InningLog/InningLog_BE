package com.inninglog.inninglog.seatView.repository;

import com.inninglog.inninglog.seatView.domain.SeatView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatViewRepository extends JpaRepository<SeatView, Long> {

    // 좌석 정보 기반 검색
    @Query("SELECT sv FROM SeatView sv " +
            "JOIN FETCH sv.zone z " +
            "JOIN FETCH sv.stadium s " +
            "WHERE s.shortCode = :stadiumShortCode " +
            "AND (:zoneShortCode IS NULL OR z.shortCode = :zoneShortCode) " +
            "AND (:section IS NULL OR sv.section = :section) " +
            "AND (:seatRow IS NULL OR sv.seatRow = :seatRow)")
    List<SeatView> findSeatViewsBySearchCriteria(
            @Param("stadiumShortCode") String stadiumShortCode,
            @Param("zoneShortCode") String zoneShortCode,
            @Param("section") String section,
            @Param("seatRow") String seatRow
    );



    // 해시태그 기반 검색 - 모아보기용 (사진만) - OR 조건
    @Query("SELECT DISTINCT sv FROM SeatView sv " +
            "JOIN sv.stadium s " +
            "JOIN SeatViewEmotionTagMap setm ON setm.seatView.id = sv.id " +
            "JOIN setm.seatViewEmotionTag tag " +
            "WHERE s.shortCode = :stadiumShortCode " +
            "AND tag.code IN :hashtagCodes")
    List<SeatView> findSeatViewsByHashtagsOr(
            @Param("stadiumShortCode") String stadiumShortCode,
            @Param("hashtagCodes") List<String> hashtagCodes
    );

    // 해시태그 기반 검색 - 모아보기용 (사진만) - AND 조건
    @Query("SELECT sv FROM SeatView sv " +
            "JOIN sv.stadium s " +
            "WHERE s.shortCode = :stadiumShortCode " +
            "AND (SELECT COUNT(DISTINCT setm.seatViewEmotionTag.code) " +
            "     FROM SeatViewEmotionTagMap setm " +
            "     WHERE setm.seatView.id = sv.id " +
            "     AND setm.seatViewEmotionTag.code IN :hashtagCodes) = :hashtagCount")
    List<SeatView> findSeatViewsByHashtagsAnd(
            @Param("stadiumShortCode") String stadiumShortCode,
            @Param("hashtagCodes") List<String> hashtagCodes,
            @Param("hashtagCount") long hashtagCount
    );

    // 해시태그 기반 검색 - 게시물 형태용 (상세 정보 포함) - AND 조건
    @Query("SELECT sv FROM SeatView sv " +
            "JOIN FETCH sv.zone z " +
            "JOIN FETCH sv.stadium s " +
            "WHERE s.shortCode = :stadiumShortCode " +
            "AND (SELECT COUNT(DISTINCT setm.seatViewEmotionTag.code) " +
            "     FROM SeatViewEmotionTagMap setm " +
            "     WHERE setm.seatView.id = sv.id " +
            "     AND setm.seatViewEmotionTag.code IN :hashtagCodes) = :hashtagCount")
    List<SeatView> findSeatViewsByHashtagsWithDetailsAnd(
            @Param("stadiumShortCode") String stadiumShortCode,
            @Param("hashtagCodes") List<String> hashtagCodes,
            @Param("hashtagCount") long hashtagCount
    );



}
