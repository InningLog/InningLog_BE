package com.inninglog.inninglog.domain.like.controller;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.like.service.LikeUsecase;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "좋아요", description = "좋아요 관련 API")
public class LIkeCreateController {
    private final LikeUsecase likeUsecase;

    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<SuccessResponse<Void>> createLikeAtPost(
        @PathVariable Long postId,
        @AuthenticationPrincipal CustomUserDetails user
    ){
        likeUsecase.createLike(ContentType.POST,postId,user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}
