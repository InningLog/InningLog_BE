package com.inninglog.inninglog.domain.contentImage.dto.req;

import java.util.List;

public record ImageListUploadReqDto(
        List<ImageUploadReqDto> imageUploadReqDto
) {
}
