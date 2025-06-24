package com.inninglog.inninglog.kbo.dto.gameSchdule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


// 응답 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameScheduleResponseDto {
    private boolean success;
    private String message;
    private int savedCount;       // 신규 저장된 경기 수
    private int updatedCount;     // 업데이트된 경기 수
    private int duplicateCount;   // 중복 경기 수
    private int errorCount;       // 오류 경기 수
    private List<String> errorMessages;

    public static GameScheduleResponseDto success(String message, int savedCount, int updatedCount, int duplicateCount) {
        return GameScheduleResponseDto.builder()
                .success(true)
                .message(message)
                .savedCount(savedCount)
                .updatedCount(updatedCount)
                .duplicateCount(duplicateCount)
                .errorCount(0)
                .build();
    }

    public static GameScheduleResponseDto error(String message) {
        return GameScheduleResponseDto.builder()
                .success(false)
                .message(message)
                .savedCount(0)
                .updatedCount(0)
                .duplicateCount(0)
                .errorCount(1)
                .build();
    }
}