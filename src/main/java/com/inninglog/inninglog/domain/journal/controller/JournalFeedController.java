package com.inninglog.inninglog.domain.journal.controller;

import com.inninglog.inninglog.domain.journal.dto.res.JournalFeedResDto;
import com.inninglog.inninglog.domain.journal.usecase.JournalUsecase;
import com.inninglog.inninglog.global.dto.SliceResponse;
import com.inninglog.inninglog.global.exception.ErrorApiResponses;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
@Tag(name = "피드", description = "직관 일지 피드 API (인증 필요)")
public class JournalFeedController {

    private final JournalUsecase journalUsecase;

    @Operation(
            summary = "공개 직관 일지 피드 조회",
            description = """
                공개 설정된 직관 일지를 조회합니다. 인증이 필요합니다.

                📌 **팀 필터링**
                - `teamShortCode` 파라미터로 특정 팀(작성자 응원팀 기준) 일지만 조회 가능
                - 미지정 시 전체 공개 일지 조회

                📌 **페이지네이션**
                - 무한 스크롤 방식 (Slice 기반)
                - `page`, `size` 파라미터로 제어

                ✅ 예시 요청:
                - 전체 조회: `/feed/journals?page=0&size=10`
                - LG팬 일지만: `/feed/journals?teamShortCode=LG&page=0&size=10`
                """
    )
    @ErrorApiResponses.Common
    @ApiResponse(
            responseCode = "200",
            description = "피드 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SliceResponse.class),
                    examples = {
                            @ExampleObject(name = "피드 목록", value = """
                                {
                                  "code": "SUCCESS",
                                  "message": "요청이 정상적으로 처리되었습니다.",
                                  "data": {
                                    "content": [
                                      {
                                        "journalId": 123,
                                        "thumbnailUrl": "https://s3.amazonaws.com/.../image.jpg",
                                        "member": {
                                          "nickName": "볼빨간스트라스버그",
                                          "profile_url": "https://k.kakaocdn.net/.../img.jpg"
                                        },
                                        "reviewPreview": "오늘 경기 정말 재밌었다! 우리 팀이 역전승...",
                                        "createdAt": "2025-06-03 18:30",
                                        "likeCount": 15,
                                        "commentCount": 3,
                                        "scrapCount": 2
                                      }
                                    ],
                                    "hasNext": true,
                                    "page": 0,
                                    "size": 10
                                  }
                                }
                                """),
                            @ExampleObject(name = "피드 없음", value = """
                                {
                                  "code": "SUCCESS",
                                  "message": "요청이 정상적으로 처리되었습니다.",
                                  "data": {
                                    "content": [],
                                    "hasNext": false,
                                    "page": 0,
                                    "size": 10
                                  }
                                }
                                """)
                    }
            )
    )
    @GetMapping("/journals")
    public ResponseEntity<SuccessResponse<SliceResponse<JournalFeedResDto>>> getPublicJournalFeed(
            @Parameter(description = "팀 숏코드 (작성자 응원팀 기준 필터)", example = "LG")
            @RequestParam(required = false) String teamShortCode,

            @Parameter(
                    description = "페이지 번호 (0부터 시작)",
                    example = "0",
                    schema = @Schema(type = "integer", minimum = "0")
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    description = "페이지 크기",
                    example = "10",
                    schema = @Schema(type = "integer", minimum = "1", maximum = "100")
            )
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        SliceResponse<JournalFeedResDto> result = journalUsecase.getPublicJournalFeed(teamShortCode, pageable);

        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, result));
    }
}
