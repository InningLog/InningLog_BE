package com.inninglog.inninglog.domain.searchHistory.controller;

import com.inninglog.inninglog.domain.searchHistory.dto.res.SearchHistoryResDto;
import com.inninglog.inninglog.domain.searchHistory.service.SearchHistoryService;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
@Tag(name = "검색", description = "검색 관련 API")
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    @Operation(
            summary = "최근 검색어 조회",
            description = """
                로그인한 유저의 최근 검색어를 최대 12개 조회합니다.

                ✔ 최신순(createdAt DESC)으로 정렬
                ✔ 동일 키워드 재검색 시 최신으로 갱신
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "최근 검색어 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                {
                                  "code": "SUCCESS",
                                  "message": "요청이 정상적으로 처리되었습니다.",
                                  "data": [
                                    { "id": 15, "keyword": "역전승", "createdAt": "2026-02-26T14:30:00" },
                                    { "id": 12, "keyword": "잠실", "createdAt": "2026-02-26T13:00:00" }
                                  ]
                                }
                                """)
                    )
            )
    })
    @GetMapping("/history")
    public ResponseEntity<SuccessResponse<List<SearchHistoryResDto>>> getRecentSearches(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        List<SearchHistoryResDto> result = searchHistoryService.getRecentSearches(user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, result));
    }

    @Operation(
            summary = "검색어 삭제",
            description = "특정 검색어를 삭제합니다. 본인의 검색어만 삭제 가능합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색어 삭제 성공")
    })
    @DeleteMapping("/history/{searchHistoryId}")
    public ResponseEntity<SuccessResponse<Void>> deleteSearchHistory(
            @Parameter(description = "검색 기록 ID", example = "1")
            @PathVariable Long searchHistoryId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        searchHistoryService.deleteSearchHistory(searchHistoryId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, null));
    }
}
