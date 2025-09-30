package com.inninglog.inninglog.domain.kbo.domain;

public enum GameStatus {
    SCHEDULED,    // 예정
    IN_PROGRESS,  // 진행중 (향후 확장용)
    COMPLETED,    // 완료
    POSTPONED,    // 연기 (향후 확장용)
    CANCELLED     // 취소 (향후 확장용)
}