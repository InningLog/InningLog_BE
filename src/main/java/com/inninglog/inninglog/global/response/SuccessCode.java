package com.inninglog.inninglog.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {

    OK("SUCCESS", HttpStatus.OK, "요청이 정상적으로 처리되었습니다."),
    LOGIN_SUCCESS("LOGIN_SUCCESS", HttpStatus.OK, "로그인이 성공적으로 되었습니다."),
    NICKNAME_UPDATED("NICKNAME_UPDATED", HttpStatus.OK, "닉네임이 성공적으로 수정되었습니다."),
    TEAM_SET("TEAM_SET", HttpStatus.OK, "응원 팀이 성공적으로 설정되었습니다."),

    NO_SCHEDULE_ON_DATE("NO_SCHEDULE_ON_DATE", HttpStatus.OK, "해당 날짜에 예정된 경기가 없습니다."),
    S3_UPLOAD_SUCCESS("S3_UPLOAD_SUCCESS", HttpStatus.OK, "이미지 업로드가 성공적으로 완료되었습니다."),
    JOURNAL_CREATED("JOURNAL_CREATED", HttpStatus.CREATED, "직관 일지가 등록되었습니다."),
    REPORT_GENERATED("REPORT_GENERATED", HttpStatus.OK, "리포트가 생성되었습니다."),
    JOURNAL_LIST_FETCHED("JOURNAL_LIST_FETCHED", HttpStatus.OK, "직관 일지 리스트 조회 성공"),
    JOURNAL_EMPTY("JOURNAL_EMPTY", HttpStatus.OK, "해당 조건에 해당하는 직관 일지가 없습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    SuccessCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}