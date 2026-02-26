package com.inninglog.inninglog.domain.post.controller;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.post.dto.res.CommunityHomeResDto;
import com.inninglog.inninglog.domain.post.dto.res.PostSingleResDto;
import com.inninglog.inninglog.domain.post.dto.res.PostSummaryResDto;
import com.inninglog.inninglog.domain.post.service.PostUsecase;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.dto.SliceResponse;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "커뮤니티 - 게시글", description = "게시글 관련 API")
public class PostGetController {
    private final PostUsecase postUsecase;

    @Operation(
            summary = "커뮤니티 홈 조회",
            description = """
                커뮤니티 홈 화면에 필요한 데이터를 조회합니다.

                - 내 응원팀 숏 코드
                - 인기 게시글 최신 2개 (좋아요 10개 이상)
                  - 작성자 정보 제외
                  - 내가 좋아요 누른 여부 (likedByMe)
                  - 내가 스크랩한 여부 (scrapedByMe)
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "커뮤니티 홈 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommunityHomeResDto.class),
                            examples = {
                                    @ExampleObject(name = "커뮤니티 홈", value = """
                                        {
                                          "code": "SUCCESS",
                                          "message": "요청이 정상적으로 처리되었습니다.",
                                          "data": {
                                            "supportTeamShortCode": "LG",
                                            "popularPosts": [
                                              {
                                                "postId": 45,
                                                "teamShortCode": "LG",
                                                "title": "역전승 후기",
                                                "content": "9회말 투런홈런!",
                                                "likeCount": 24,
                                                "scrapCount": 5,
                                                "commentCount": 18,
                                                "thumbImageUrl": "https://s3.amazonaws.com/.../thumb.jpg",
                                                "imageCount": 3,
                                                "postAt": "2025-06-03 18:30",
                                                "likedByMe": true,
                                                "scrapedByMe": false
                                              }
                                            ]
                                          }
                                        }
                                        """)
                            }
                    )
            )
    })
    @GetMapping("/home")
    public ResponseEntity<SuccessResponse<CommunityHomeResDto>> getCommunityHome(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        CommunityHomeResDto result = postUsecase.getCommunityHome(user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, result));
    }

    @GetMapping("/posts/{postId}")
    @Operation(
            summary = "게시글 단일 조회",
            description = """
                특정 팀에 속한 게시글을 단건 조회합니다.
                
                - 이미지 목록 포함
                - 작성자 정보 포함
                - 포맷팅된 작성일(postAt) 포함 (yyyy-MM-dd HH:mm)
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
    })
    public ResponseEntity<SuccessResponse<PostSingleResDto>> getSinglePost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable("postId") Long postId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        PostSingleResDto resdto = postUsecase.getSinglePost(ContentType.POST, postId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, resdto));
    }


    @Operation(
            summary = "팀별 게시글 목록 조회",
            description = """
                특정 팀의 게시글 목록을 Slice 기반으로 조회합니다.

                ✔ 최신순(postAt DESC)으로 정렬되어 반환됩니다.  
                ✔ page는 0부터 시작합니다. (0=첫 페이지)  
                ✔ size는 한 페이지에서 가져올 게시글 수를 의미합니다.  
                ✔ hasNext가 true이면 다음 페이지 요청이 가능합니다.

                ※ 목록에서는 본문(content)은 전체 원문이 내려가며,  
                   실제 UI에서 최대 24글자로 잘라 사용하는 것을 권장합니다.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "팀별 게시글 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SliceResponse.class),
                            examples = {
                                    @ExampleObject(name = "게시글 목록", value = """
                                        {
                                          "code": "SUCCESS",
                                          "message": "요청이 정상적으로 처리되었습니다.",
                                          "data": {
                                            "content": [
                                              {
                                                "postId": 45,
                                                "teamShortCode": "LG",
                                                "title": "오늘 경기 직관 후기",
                                                "content": "오늘 잠실 가서 경기 봤는데 역전승해서 너무 좋았어요!",
                                                "member": {
                                                  "nickName": "볼빨간스트라스버그",
                                                  "profile_url": "https://k.kakaocdn.net/.../img.jpg"
                                                },
                                                "likeCount": 12,
                                                "scrapCount": 3,
                                                "commentCount": 8,
                                                "thumbImageUrl": "https://s3.amazonaws.com/.../thumb.jpg",
                                                "imageCount": 3,
                                                "postAt": "2025-06-03 18:30"
                                              }
                                            ],
                                            "hasNext": true,
                                            "page": 0,
                                            "size": 10
                                          }
                                        }
                                        """),
                                    @ExampleObject(name = "게시글 없음", value = """
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
    })
    @GetMapping("/posts/team/{teamShortCode}")
    public ResponseEntity<SuccessResponse<SliceResponse<PostSummaryResDto>>> getPostsByTeam(
            @Parameter(description = "팀 숏 코드 (예: LG, OB, LT)", example = "LG")
            @PathVariable String teamShortCode,

            @Parameter(description = "조회할 페이지 번호 (0부터 시작)", example = "0")
            @RequestParam int page,

            @Parameter(description = "한 페이지당 게시글 개수", example = "10")
            @RequestParam int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        SliceResponse<PostSummaryResDto> resdto = postUsecase.getPostList(teamShortCode, pageable);

        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, resdto));
    }

    @Operation(
            summary = "인기 게시글 목록 조회",
            description = """
                좋아요가 10개 이상인 인기 게시글 목록을 조회합니다.

                ✔ 최신순(postAt DESC)으로 정렬되어 반환됩니다.
                ✔ page는 0부터 시작합니다. (0=첫 페이지)
                ✔ size는 한 페이지에서 가져올 게시글 수를 의미합니다.
                ✔ hasNext가 true이면 다음 페이지 요청이 가능합니다.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "인기 게시글 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SliceResponse.class),
                            examples = {
                                    @ExampleObject(name = "인기 게시글 목록", value = """
                                        {
                                          "code": "SUCCESS",
                                          "message": "요청이 정상적으로 처리되었습니다.",
                                          "data": {
                                            "content": [
                                              {
                                                "postId": 45,
                                                "teamShortCode": "LG",
                                                "title": "오늘 경기 역전승 후기",
                                                "content": "9회말 역전 투런홈런! 잠실이 터졌어요!",
                                                "member": {
                                                  "nickName": "볼빨간스트라스버그",
                                                  "profile_url": "https://k.kakaocdn.net/.../img.jpg"
                                                },
                                                "likeCount": 24,
                                                "scrapCount": 5,
                                                "commentCount": 18,
                                                "thumbImageUrl": "https://s3.amazonaws.com/.../thumb.jpg",
                                                "imageCount": 3,
                                                "postAt": "2025-06-03 18:30"
                                              }
                                            ],
                                            "hasNext": true,
                                            "page": 0,
                                            "size": 10
                                          }
                                        }
                                        """)
                            }
                    )
            )
    })
    @GetMapping("/posts/popular")
    public ResponseEntity<SuccessResponse<SliceResponse<PostSummaryResDto>>> getPopularPosts(
            @Parameter(description = "조회할 페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "한 페이지당 게시글 개수", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Pageable pageable = PageRequest.of(page, size);
        SliceResponse<PostSummaryResDto> result = postUsecase.getPopularPostList(pageable);

        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, result));
    }

    @Operation(
            summary = "게시글 검색",
            description = """
                키워드로 게시글을 검색합니다.

                📌 **검색 대상**: 제목 (title) + 본문 (content)
                📌 **정렬**: 최신순 (postAt DESC)
                📌 **페이지네이션**: Slice 기반 무한 스크롤

                ✅ 예시: `/community/posts/search?keyword=직관&page=0&size=10`
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "게시글 검색 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SliceResponse.class)
                    )
            )
    })
    @GetMapping("/posts/search")
    public ResponseEntity<SuccessResponse<SliceResponse<PostSummaryResDto>>> searchPosts(
            @Parameter(description = "검색 키워드", example = "직관")
            @RequestParam String keyword,

            @Parameter(description = "조회할 페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "한 페이지당 게시글 개수", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Pageable pageable = PageRequest.of(page, size);
        SliceResponse<PostSummaryResDto> result = postUsecase.searchPosts(user.getMember(), keyword, pageable);

        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, result));
    }

    @Operation(
            summary = "내가 쓴 글 목록 조회",
            description = """
                내가 작성한 게시글 목록을 조회합니다.

                ✔ 최신순(postAt DESC)으로 정렬되어 반환됩니다.
                ✔ page는 0부터 시작합니다. (0=첫 페이지)
                ✔ size는 한 페이지에서 가져올 게시글 수를 의미합니다.
                ✔ hasNext가 true이면 다음 페이지 요청이 가능합니다.
                """,
            tags = {"마이페이지"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "내가 쓴 글 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SliceResponse.class),
                            examples = {
                                    @ExampleObject(name = "내가 쓴 글 목록", value = """
                                        {
                                          "code": "SUCCESS",
                                          "message": "요청이 정상적으로 처리되었습니다.",
                                          "data": {
                                            "content": [
                                              {
                                                "postId": 45,
                                                "teamShortCode": "LG",
                                                "title": "오늘 경기 직관 후기",
                                                "content": "오늘 잠실 가서 경기 봤는데 역전승해서 너무 좋았어요!",
                                                "member": {
                                                  "nickName": "볼빨간스트라스버그",
                                                  "profile_url": "https://k.kakaocdn.net/.../img.jpg"
                                                },
                                                "likeCount": 12,
                                                "scrapCount": 3,
                                                "commentCount": 8,
                                                "thumbImageUrl": "https://s3.amazonaws.com/.../thumb.jpg",
                                                "imageCount": 3,
                                                "postAt": "2025-06-03 18:30",
                                                "likedByMe": true,
                                                "scrapedByMe": false
                                              }
                                            ],
                                            "hasNext": true,
                                            "page": 0,
                                            "size": 10
                                          }
                                        }
                                        """)
                            }
                    )
            )
    })
    @GetMapping("/posts/my")
    public ResponseEntity<SuccessResponse<SliceResponse<PostSummaryResDto>>> getMyPosts(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "조회할 페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "한 페이지당 게시글 개수", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        SliceResponse<PostSummaryResDto> result = postUsecase.getMyPosts(user.getMember(), pageable);

        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, result));
    }

    @Operation(
            summary = "내가 댓글 단 글 목록 조회",
            description = """
                내가 댓글을 작성한 게시글 목록을 조회합니다.

                ✔ 최신 댓글을 단 게시글 순으로 정렬되어 반환됩니다.
                ✔ 같은 게시글에 여러 댓글을 달아도 중복 없이 1번만 표시됩니다.
                ✔ page는 0부터 시작합니다. (0=첫 페이지)
                ✔ size는 한 페이지에서 가져올 게시글 수를 의미합니다.
                ✔ hasNext가 true이면 다음 페이지 요청이 가능합니다.
                """,
            tags = {"마이페이지"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "내가 댓글 단 글 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SliceResponse.class),
                            examples = {
                                    @ExampleObject(name = "내가 댓글 단 글 목록", value = """
                                        {
                                          "code": "SUCCESS",
                                          "message": "요청이 정상적으로 처리되었습니다.",
                                          "data": {
                                            "content": [
                                              {
                                                "postId": 32,
                                                "teamShortCode": "OB",
                                                "title": "두산 팬들 모여라",
                                                "content": "오늘 경기 어떻게 보셨나요?",
                                                "member": {
                                                  "nickName": "두산베어스팬",
                                                  "profile_url": "https://k.kakaocdn.net/.../img.jpg"
                                                },
                                                "likeCount": 5,
                                                "scrapCount": 1,
                                                "commentCount": 15,
                                                "thumbImageUrl": null,
                                                "imageCount": 0,
                                                "postAt": "2025-06-02 20:15",
                                                "likedByMe": false,
                                                "scrapedByMe": true
                                              }
                                            ],
                                            "hasNext": false,
                                            "page": 0,
                                            "size": 10
                                          }
                                        }
                                        """)
                            }
                    )
            )
    })
    @GetMapping("/posts/my/commented")
    public ResponseEntity<SuccessResponse<SliceResponse<PostSummaryResDto>>> getMyCommentedPosts(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "조회할 페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "한 페이지당 게시글 개수", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        SliceResponse<PostSummaryResDto> result = postUsecase.getMyCommentedPosts(user.getMember(), pageable);

        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, result));
    }

    @Operation(
            summary = "내가 스크랩한 글 목록 조회",
            description = """
                내가 스크랩한 게시글 목록을 조회합니다.

                ✔ 최근 스크랩한 순으로 정렬되어 반환됩니다.
                ✔ page는 0부터 시작합니다. (0=첫 페이지)
                ✔ size는 한 페이지에서 가져올 게시글 수를 의미합니다.
                ✔ hasNext가 true이면 다음 페이지 요청이 가능합니다.
                """,
            tags = {"마이페이지"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "내가 스크랩한 글 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SliceResponse.class),
                            examples = {
                                    @ExampleObject(name = "내가 스크랩한 글 목록", value = """
                                        {
                                          "code": "SUCCESS",
                                          "message": "요청이 정상적으로 처리되었습니다.",
                                          "data": {
                                            "content": [
                                              {
                                                "postId": 58,
                                                "teamShortCode": "LG",
                                                "title": "잠실 좌석 추천",
                                                "content": "1루 응원석 뷰가 정말 좋아요!",
                                                "member": {
                                                  "nickName": "야구덕후",
                                                  "profile_url": "https://k.kakaocdn.net/.../img.jpg"
                                                },
                                                "likeCount": 28,
                                                "scrapCount": 12,
                                                "commentCount": 7,
                                                "thumbImageUrl": "https://s3.amazonaws.com/.../view.jpg",
                                                "imageCount": 5,
                                                "postAt": "2025-06-01 14:00",
                                                "likedByMe": true,
                                                "scrapedByMe": true
                                              }
                                            ],
                                            "hasNext": true,
                                            "page": 0,
                                            "size": 10
                                          }
                                        }
                                        """)
                            }
                    )
            )
    })
    @GetMapping("/posts/my/scrapped")
    public ResponseEntity<SuccessResponse<SliceResponse<PostSummaryResDto>>> getMyScrappedPosts(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "조회할 페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "한 페이지당 게시글 개수", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        SliceResponse<PostSummaryResDto> result = postUsecase.getMyScrappedPosts(user.getMember(), pageable);

        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, result));
    }
}
