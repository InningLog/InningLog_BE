package com.inninglog.inninglog.domain.scrap.controller;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.scrap.service.ScrapUsecase;
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
@Tag(name = "스크랩", description = "스크랩 관련 API")
public class ScrapCreateController {

    private final ScrapUsecase scrapUsecase;

    @PostMapping("/posts/{postsId}/scraps")
    public ResponseEntity<SuccessResponse<Void>> createScrapAtPost(
            @PathVariable Long postsId,
            @AuthenticationPrincipal CustomUserDetails user
            ){
        scrapUsecase.createScrap(ContentType.POST, postsId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}
