package com.inninglog.inninglog.global.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface ErrorApiResponses {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "유효성 검사 실패", value = """
                                            {
                                                "code": "VALIDATION_ERROR",
                                                "message": "요청값이 올바르지 않습니다."
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "사용자 없음", value = """
                                            {
                                                "code": "USER_NOT_FOUND",
                                                "message": "존재하지 않는 회원입니다."
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "서버 오류", value = """
                                            {
                                                "code": "INTERNAL_SERVER_ERROR",
                                                "message": "서버에 문제가 발생했습니다."
                                            }
                                            """)
                            }))
    })
    //공통
    public @interface Common {
    }



    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "빈 파일", value = """
                                            {
                                                "code": "FILE_IS_EMPTY",
                                                "message": "업로드할 파일이 없습니다."
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "S3 업로드 실패", value = """
                                            {
                                                "code": "S3_UPLOAD_FAILED",
                                                "message": "이미지 업로드 중 오류가 발생했습니다."
                                            }
                                            """)
                            }))
    })
    //S3 업로드
    public @interface S3Failed {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "팀 없음", value = """
                                            {
                                                "code": "TEAM_NOT_FOUND",
                                                "message": "등록되지 않은 팀입니다."
                                            }
                                            """),
                                    @ExampleObject(name = "경기장 없음", value = """
                                            {
                                                "code": "STADIUM_NOT_FOUND",
                                                "message": "등록되지 않은 경기장입니다."
                                            }
                                            """),
                                    @ExampleObject(name = "게임 없음", value = """
                                            {
                                                "code": "GAME_NOT_FOUND",
                                                "message": "등록되지 않은 게임입니다."
                                            }
                                            """)
                            }))
    })
    public @interface Game {
    }

    // 팀 설정 관련 에러
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "이미 팀이 설정됨",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "이미 설정됨", value = """
                                    {
                                        "code": "ALREADY_SET",
                                        "message": "이미 팀이 설정되었습니다."
                                    }
                                    """)
                    ))
    })
    public @interface TeamSetting {
    }


    // 닉네임 관련 에러
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "중복 닉네임", value = """
                                            {
                                                "code": "DUPLICATE_NICKNAME",
                                                "message": "이미 존재하는 닉네임입니다."
                                            }
                                            """),
                                    @ExampleObject(name = "잘못된 닉네임 형식", value = """
                                            {
                                                "code": "INVALID_NICKNAME",
                                                "message": "닉네임 형식이 올바르지 않습니다."
                                            }
                                            """),
                            }))
    })
    public @interface Nickname {
    }

    // 게임 API 전용 (공통 에러 포함)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "유효성 검사 실패", value = """
                                            {
                                                "code": "VALIDATION_ERROR",
                                                "message": "요청값이 올바르지 않습니다."
                                            }
                                            """),
                                    @ExampleObject(name = "직관 경기 없음", value = """
                                            {
                                                "code": "NO_VISITED_GAME",
                                                "message": "직관한 경기가 없습니다."
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "사용자 없음", value = """
                                            {
                                                "code": "USER_NOT_FOUND",
                                                "message": "존재하지 않는 회원입니다."
                                            }
                                            """),
                                    @ExampleObject(name = "팀 없음", value = """
                                            {
                                                "code": "TEAM_NOT_FOUND",
                                                "message": "등록되지 않은 팀입니다."
                                            }
                                            """),
                                    @ExampleObject(name = "경기장 없음", value = """
                                            {
                                                "code": "STADIUM_NOT_FOUND",
                                                "message": "등록되지 않은 경기장입니다."
                                            }
                                            """),
                                    @ExampleObject(name = "게임 없음", value = """
                                            {
                                                "code": "GAME_NOT_FOUND",
                                                "message": "등록되지 않은 게임입니다."
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "서버 오류", value = """
                                    {
                                        "code": "INTERNAL_SERVER_ERROR",
                                        "message": "서버에 문제가 발생했습니다."
                                    }
                                    """)
                    ))
    })
    public @interface GameApi {
    }


}