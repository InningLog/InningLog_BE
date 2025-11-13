package com.inninglog.inninglog.domain.post.controller;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.post.dto.res.PostSingleResDto;
import com.inninglog.inninglog.domain.post.service.PostUsecase;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "게시글", description = "게시글 관련 API")
public class PostGetController {
    private final PostUsecase postUsecase;

    @GetMapping("/{teamSC}/posts/{postId}")
    public ResponseEntity<SuccessResponse<PostSingleResDto>> getSinglePost(
            @Parameter(description = "팀 숏 코드 (ex: LG)", example = "LG")
            @PathVariable("teamSC") String teamShortCode,

            @Parameter(description = "게시글 Id (ex: 1)", example = "1")
            @PathVariable("postId") Long postId
    )
    {
       PostSingleResDto resdto = postUsecase.getSinglePost(ContentType.POST,postId);
       return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, resdto));
    }
}
