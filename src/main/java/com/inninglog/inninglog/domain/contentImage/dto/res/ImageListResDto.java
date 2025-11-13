package com.inninglog.inninglog.domain.contentImage.dto.res;

import java.util.List;

public record ImageListResDto(
        List<ImageResDto> imageResDtos
) {
    public static ImageListResDto of(List<ImageResDto> imageResDtos)  {
        return new ImageListResDto(imageResDtos);
    }
}
