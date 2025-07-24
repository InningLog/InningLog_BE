package com.inninglog.inninglog.seatView.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashtagSearchReq {
    private String stadiumShortCode;
    private List<String> hashtagCodes; // 최대 2개
    private Boolean isAndCondition; // true: AND 조건, false: OR 조건

    // 해시태그 개수 검증
    public boolean isValidRequest() {
        return hashtagCodes != null &&
                !hashtagCodes.isEmpty() &&
                hashtagCodes.size() <= 2;
    }

    // AND 조건 여부 확인 (기본값: false - OR 조건)
    public boolean isAndCondition() {
        return isAndCondition != null && isAndCondition;
    }

    public static HashtagSearchReq from(String stadiumShortCode, List<String> hashtagCodes, Boolean isAndCondition) {
        return HashtagSearchReq.builder()
                .stadiumShortCode(stadiumShortCode)
                .hashtagCodes(hashtagCodes)
                .isAndCondition(isAndCondition)
                .build();
    }
}