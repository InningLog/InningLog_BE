package com.inninglog.inninglog.domain.contentImage.dto.res;

import com.inninglog.inninglog.domain.contentImage.domain.ContentImage;
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Int;

public record ImageResDto(
        Long ImageId,
        Integer sequence,
        String url
) {
    public static ImageResDto from(ContentImage contentImage) {
        return new ImageResDto(
                contentImage.getId(),
                contentImage.getSequence(),
                contentImage.getOriginalUrl()
        );
    }
}
