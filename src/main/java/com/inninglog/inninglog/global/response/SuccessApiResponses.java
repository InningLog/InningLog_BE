package com.inninglog.inninglog.global.response;

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

    // 사용자 관련 성공 응답
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 업데이트 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(name = "닉네임 수정", value = """
                                            {
                                              "code": "NICKNAME_UPDATED",
                                              "message": "닉네임이 성공적으로 수정되었습니다.",
                                              "data": {
                                                null
                                              }
                                            }
                                            """),
                                    @ExampleObject(name = "팀 설정", value = """
                                            {
                                              "code": "TEAM_SET",
                                              "message": "응원 팀이 성공적으로 설정되었습니다.",
                                              "data": {
                                                null
                                              }
                                            }
                                            """)
                            }))
    })
    public @interface UserUpdate {
    }

    // 일지 생성 성공 응답
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "일지 생성 성공",
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
    public @interface JournalCreate {
    }

    // 일지 조회 성공 응답
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일지 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(name = "일지 목록 있음", value = """
                                            {
                                              "code": "JOURNAL_LIST_FETCHED",
                                              "message": "직관 일지 리스트 조회 성공",
                                              "data": [
                                                {
                                                  "journalId": 123,
                                                  "gameDate": "2024-06-27",
                                                  "stadium": "잠실야구장",
                                                  "homeTeam": "두산 베어스",
                                                  "awayTeam": "LG 트윈스",
                                                  "score": "7-3"
                                                }
                                              ]
                                            }
                                            """),
                                    @ExampleObject(name = "일지 목록 없음", value = """
                                            {
                                              "code": "JOURNAL_EMPTY",
                                              "message": "해당 조건에 해당하는 직관 일지가 없습니다.",
                                              "data": []
                                            }
                                            """)
                            }))
    })
    public @interface JournalList {
    }

    // 파일 업로드 성공 응답
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일 업로드 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "code": "S3_UPLOAD_SUCCESS",
                                      "message": "이미지 업로드가 성공적으로 완료되었습니다.",
                                      "data": {
                                        "url": "https://s3.amazonaws.com/bucket/images/journal_123.jpg",
                                      }
                                    }
                                    """)
                    ))
    })
    public @interface FileUpload {
    }

    // 리포트 생성 성공 응답
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리포트 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "code": "REPORT_GENERATED",
                                      "message": "리포트가 생성되었습니다.",
                                      "data": {
                                        "reportId": "REPORT_2024_06",
                                        "totalGames": 15,
                                        "favoriteStadium": "잠실야구장",
                                        "winRate": 0.73,
                                        "generatedAt": "2024-06-27T10:30:00"
                                      }
                                    }
                                    """)
                    ))
    })
    public @interface ReportGenerate {
    }

    // 조합형 어노테이션들

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
}