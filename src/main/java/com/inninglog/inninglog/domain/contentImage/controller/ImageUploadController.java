package com.inninglog.inninglog.domain.contentImage.controller;

import com.inninglog.inninglog.domain.contentImage.dto.req.ImageListUploadReqDto;
import com.inninglog.inninglog.domain.contentImage.dto.req.ImageUploadReqDto;
import com.inninglog.inninglog.domain.contentImage.dto.res.ImageListUploadResDto;
import com.inninglog.inninglog.domain.contentImage.dto.res.ImageUploadResDto;
import com.inninglog.inninglog.domain.contentImage.service.ContentImageCreateService;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
@Tag(name = "사진 리스트 업로드", description = "사진 리스트 업로드 관련 API")
public class ImageUploadController {

    private final ContentImageCreateService contentImageCreateService;

    @PostMapping("/upload/post")
    public ResponseEntity<SuccessResponse<ImageListUploadResDto>> uploadPostImage(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody ImageListUploadReqDto reqDto){
        ImageListUploadResDto dto = contentImageCreateService.getPostImagePreseignedUrlList(reqDto.imageUploadReqDto(), user.getMember().getId());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, dto));
    }
}
