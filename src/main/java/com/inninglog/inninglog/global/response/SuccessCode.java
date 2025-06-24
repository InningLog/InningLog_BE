package com.inninglog.inninglog.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {

    OK("SUCCESS", HttpStatus.OK, "요청이 정상적으로 처리되었습니다."),
    NICKNAME_UPDATED("NICKNAME_UPDATED", HttpStatus.OK, "닉네임이 성공적으로 수정되었습니다."),
    TEAM_SET("TEAM_SET", HttpStatus.OK, "응원 팀이 성공적으로 설정되었습니다."),
    JOURNAL_CREATED("JOURNAL_CREATED", HttpStatus.CREATED, "직관 일지가 등록되었습니다."),
    REPORT_GENERATED("REPORT_GENERATED", HttpStatus.OK, "리포트가 생성되었습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    SuccessCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}