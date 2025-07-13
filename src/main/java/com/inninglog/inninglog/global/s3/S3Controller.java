package com.inninglog.inninglog.global.s3;

import com.inninglog.inninglog.global.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
@Tag(name = "사진 업로드", description = "S3 Presigned URL 관련")
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/journal/presigned")
    public ResponseEntity<String> generatePresignedUrl(
            @RequestParam String fileName,
            @RequestParam String contentType,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

       String url =  s3Service.generateUrl(user.getMember().getId(), fileName,contentType);

        return ResponseEntity.ok(url);
    }
}
