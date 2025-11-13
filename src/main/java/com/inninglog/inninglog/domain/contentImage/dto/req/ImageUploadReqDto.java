package com.inninglog.inninglog.domain.contentImage.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이미지 업로드 요청 DTO")
public record ImageUploadReqDto(
        Integer sequence,
        String fileName,
        String contentType
) {
}
