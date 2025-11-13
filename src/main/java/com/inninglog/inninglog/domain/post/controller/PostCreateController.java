package com.inninglog.inninglog.domain.post.controller;

import com.inninglog.inninglog.domain.post.dto.req.PostCreateReqDto;
import com.inninglog.inninglog.domain.post.service.PostUsecase;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "게시글", description = "게시글 관련 API")
public class PostCreateController {

    private final PostUsecase postUsecase;

    @PostMapping("/{teamSC}/posts/create")
    public ResponseEntity<SuccessResponse<Void>> createPost(@PathVariable("teamSC") String teamSC,
                                                            @AuthenticationPrincipal CustomUserDetails user,
                                                            @RequestBody PostCreateReqDto dto){
        postUsecase.uploadPost(dto, teamSC,user.getMember());

        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}
