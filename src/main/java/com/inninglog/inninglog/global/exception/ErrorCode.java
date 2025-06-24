package com.inninglog.inninglog.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다."),
    VALIDATION_ERROR("VALIDATION_ERROR", HttpStatus.BAD_REQUEST, "요청값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
    TYPE_MISMATCH("TYPE_MISMATCH", HttpStatus.BAD_REQUEST, "요청 파라미터 타입이 올바르지 않습니다."),

    TEAM_NOT_FOUND("TEAM_NOT_FOUND", HttpStatus.NOT_FOUND, "등록되지 않은 팀입니다."),
    ALREADY_SET("ALREADY_SET", HttpStatus.CONFLICT, "이미 유저 타입 혹은 팀이 설정되었습니다."),
    STADIUM_NOT_FOUND("STADIUM_NOT_FOUND", HttpStatus.NOT_FOUND, "등록되지 않은 경기장입니다."),
    JOURNAL_NOT_FOUND("JOURNAL_NOT_FOUND", HttpStatus.NOT_FOUND, "작성하지 않은 일지 입니다."),
    EMOTION_TAG_NOT_FOUND("EMOTION_TAG_NOT_FOUND", HttpStatus.NOT_FOUND, "감정 태그가 없습니다."),
    ZONE_NOT_FOUND("ZONE_NOT_FOUND", HttpStatus.NOT_FOUND, "등록되지 않은 존입니다."),
    GAME_NOT_FOUND("GAME_NOT_FOUND", HttpStatus.NOT_FOUND, "등록되지 않은 게임 입니다."),
    NO_VISITED_GAMES("NO_VISITED_GAME", HttpStatus.BAD_REQUEST, "직관한 경기가 없습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}