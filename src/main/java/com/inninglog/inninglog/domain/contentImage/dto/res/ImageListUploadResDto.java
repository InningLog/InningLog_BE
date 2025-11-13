package com.inninglog.inninglog.domain.contentImage.dto.res;

import java.util.List;

public record ImageListUploadResDto(
        List<ImageUploadResDto> imageUploadResDtos
) {

    public static ImageListUploadResDto of(List<ImageUploadResDto> dtos) {
        return new ImageListUploadResDto(dtos);
    }
}