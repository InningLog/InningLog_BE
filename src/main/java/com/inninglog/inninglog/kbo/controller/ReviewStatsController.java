package com.inninglog.inninglog.kbo.controller;

import com.inninglog.inninglog.kbo.dto.ReviewStatsDto;
import com.inninglog.inninglog.kbo.service.KboHtmlReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kbo")
@RequiredArgsConstructor
public class ReviewStatsController {

    private final KboHtmlReviewService reviewService;

    /**
     * 1) 게임 ID 로만 호출하는 엔드포인트
     *    /api/kbo/games/{gameId}/stats
     *    → 내부에서 리뷰 URL 을 조립해서 크롤링
     */
    @GetMapping("/games/{gameId}/stats")
    public ResponseEntity<ReviewStatsDto> getStatsByGameId(
            @PathVariable("gameId") String gameId
    ) {
        String reviewUrl = "https://www.koreabaseball.com/GameCenter/ReviewNew.aspx"
                + "?gameId=" + gameId
                + "&section=REVIEW";
        ReviewStatsDto stats = reviewService.getReviewStats(reviewUrl);
        if (stats.getPitchers().isEmpty() && stats.getBatters().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(stats);
    }

    /**
     * 2) 리뷰 URL 전체를 직접 넘기는 엔드포인트
     *    /api/kbo/review-stats?reviewUrl=...
     */
    @GetMapping("/review-stats")
    public ResponseEntity<ReviewStatsDto> fetchByReviewUrl(
            @RequestParam("reviewUrl") String reviewUrl
    ) {
        if (reviewUrl == null || reviewUrl.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        ReviewStatsDto stats = reviewService.getReviewStats(reviewUrl);
        if (stats.getPitchers().isEmpty() && stats.getBatters().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(stats);
    }
}