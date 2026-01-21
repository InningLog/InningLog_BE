package com.inninglog.inninglog.domain.team.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamWinRateDto {

    private String team;

    @JsonProperty("winRate")
    private Double winRate;

    private String date;

    // FastAPI에서 받는 요청 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WinRateUpdateRequest {

        private String date;

        @JsonProperty("winRates")
        private List<TeamWinRateDto> winRates;

        @JsonProperty("totalTeams")
        private Integer totalTeams;

        @JsonProperty("crawledAt")
        private String crawledAt;
    }

    // 응답 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WinRateUpdateResponse {

        private String message;

        @JsonProperty("updatedTeams")
        private Integer updatedTeams;

        @JsonProperty("totalTeams")
        private Integer totalTeams;

        @JsonProperty("skippedTeams")
        private Integer skippedTeams;

        private String date;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime processedAt;

        @JsonProperty("teamDetails")
        private List<TeamUpdateDetail> teamDetails;
    }

    // 팀별 업데이트 상세 정보
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamUpdateDetail {

        @JsonProperty("teamName")
        private String teamName;

        @JsonProperty("shortCode")
        private String shortCode;

        @JsonProperty("oldWinRate")
        private Double oldWinRate;

        @JsonProperty("newWinRate")
        private Double newWinRate;

        private String status; // "UPDATED", "SKIPPED", "NOT_FOUND"

        private String message;
    }
}