package com.inninglog.inninglog.domain.seatView.repository;

import com.inninglog.inninglog.domain.seatView.domain.SeatViewEmotionTag;
import com.inninglog.inninglog.domain.seatView.dto.req.SeatViewEmotionTagDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatViewEmotionTagRepository extends JpaRepository<SeatViewEmotionTag, Long> {
Optional<SeatViewEmotionTag> findByCode(String code);

List<SeatViewEmotionTag> findByCodeIn(List<String> codes);
    @Query("SELECT new com.inninglog.inninglog.seatView.dto.req.SeatViewEmotionTagDto(t.code, t.label) " +
            "FROM SeatViewEmotionTagMap m JOIN m.seatViewEmotionTag t WHERE m.seatView.id = :seatViewId")
    List<SeatViewEmotionTagDto> findDtosBySeatViewId(@Param("seatViewId") Long seatViewId);
}