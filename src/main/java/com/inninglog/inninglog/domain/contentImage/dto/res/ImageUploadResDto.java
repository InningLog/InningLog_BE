package com.inninglog.inninglog.domain.contentImage.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이미지 업로드 url DTO")
public record ImageUploadResDto(
        Integer sequence,
        String presignedUrl
) {
}
