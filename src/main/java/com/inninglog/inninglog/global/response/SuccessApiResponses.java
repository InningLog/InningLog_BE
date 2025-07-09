package com.inninglog.inninglog.global.response;

import com.inninglog.inninglog.journal.dto.res.JourCreateResDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.inninglog.inninglog.global.response.SuccessResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface SuccessApiResponses {

    // 공통 성공 응답
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "code": "SUCCESS",
                                      "message": "요청이 정상적으로 처리되었습니다.",
                                      "data": null
                                    }
                                    """)
                    ))
    })
    public @interface Common {
    }

    // 인증 관련 성공 응답
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(name = "기존 회원 로그인", value = """
                                            {
                                              "code": "LOGIN_SUCCESS",
                                              "message": "로그인이 성공적으로 되었습니다.",
                                              "data": {
                                                "nickname": "야구팬123",
                                                "newMember": false
                                              }
                                            }
                                            """),
                                    @ExampleObject(name = "신규 회원 로그인", value = """
                                            {
                                              "code": "LOGIN_SUCCESS",
                                              "message": "로그인이 성공적으로 되었습니다.",
                                              "data": {
                                                "nickname": "카카오_1234567890",
                                                "newMember": true
                                              }
                                            }
                                            """)
                            }),
                    headers = {
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "Authorization",
                                    description = "Bearer {accessToken}",
                                    schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                            ),
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "Refresh-Token",
                                    description = "Refresh Token",
                                    schema = @Schema(type = "string", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                            )
                    })
    })
    public @interface Login {
    }




    // 파일 업로드 성공 응답
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일 업로드 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "S3_UPLOAD_SUCCESS",
                  "message": "이미지 업로드가 성공적으로 완료되었습니다.",
                  "data": {
                    "url": "https://s3.amazonaws.com/bucket/images/journal_123.jpg"
                  }
                }
                """
                            )
                    )
            )
    })
    public @interface FileUpload {}




    // 사용자 API (로그인 + 업데이트)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(name = "로그인 성공", value = """
                                            {
                                              "code": "LOGIN_SUCCESS",
                                              "message": "로그인이 성공적으로 되었습니다.",
                                              "data": {
                                                "nickname": "야구팬123",
                                                "isNewMember": false
                                              }
                                            }
                                            """),
                                    @ExampleObject(name = "닉네임 수정", value = """
                                            {
                                              "code": "NICKNAME_UPDATED",
                                              "message": "닉네임이 성공적으로 수정되었습니다.",
                                              "data": {
                                                "nickname": "새닉네임123"
                                              }
                                            }
                                            """),
                                    @ExampleObject(name = "팀 설정", value = """
                                            {
                                              "code": "TEAM_SET",
                                              "message": "응원 팀이 성공적으로 설정되었습니다.",
                                              "data": {
                                                "teamId": 1,
                                                "teamName": "두산 베어스"
                                              }
                                            }
                                            """)
                            }))
    })
    public @interface UserApi {
    }

    // 일지 API (생성 + 조회)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(name = "일지 목록", value = """
                                            {
                                              "code": "JOURNAL_LIST_FETCHED",
                                              "message": "직관 일지 리스트 조회 성공",
                                              "data": []
                                            }
                                            """),
                                    @ExampleObject(name = "일지 없음", value = """
                                            {
                                              "code": "JOURNAL_EMPTY",
                                              "message": "해당 조건에 해당하는 직관 일지가 없습니다.",
                                              "data": []
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(name = "일지 생성", value = """
                                    {
                                      "code": "JOURNAL_CREATED",
                                      "message": "직관 일지가 등록되었습니다.",
                                      "data": {
                                        "journalId": 123
                                      }
                                    }
                                    """)
                    ))
    })
    public @interface JournalApi {
    }


    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(
            responseCode = "200",
            description = "요청이 정상적으로 처리되었습니다.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SuccessResponse.class),
                    examples = @ExampleObject(
                            name = "직관 콘텐츠 사전 정보 응답 예시",
                            summary = "성공 응답",
                            value = """
                                {
                                  "code": "SUCCESS",
                                  "message": "요청이 정상적으로 처리되었습니다.",
                                  "data": {
                                    "gameId": "20250625OBLG0",
                                    "gameDate": "2025-06-25T18:30:00",
                                    "supportTeamSC": "LG",
                                    "opponentTeamSC": "OB",
                                    "stadiumSC": "JAM"
                                  }
                                }
                                """
                    )
            )
    )
    public @interface JournalInfo {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "경기 일정 조회 성공 (또는 해당일에 경기 없음)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "경기 있음 예시",
                                            value = """
                    {
                      "code": "SUCCESS",
                      "status": 200,
                      "message": "요청이 정상적으로 처리되었습니다.",
                      "data": {
                        "gameId": "20250701OBLT0",
                        "gameDate": "2025-07-01T18:30:00",
                        "opponentSC": "LT",
                        "stadiumSC": "JAM"
                      }
                    }
                    """
                                    ),
                                    @ExampleObject(
                                            name = "경기 없음 예시",
                                            value = """
                    {
                      "code": "SUCCESS",
                      "status": 200,
                      "message": "요청이 정상적으로 처리되었습니다.",
                      "data": null
                    }
                    """
                                    )
                            }
                    )
            )
    })
    public @interface GameSchedule {}
}