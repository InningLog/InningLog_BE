package com.inninglog.inninglog.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ThumbnailUrlGenerator {

    private final S3UrlProperties s3UrlProperties;

    public String generateThumbnailUrl(String key) {
        return s3UrlProperties.getBaseUrl() + "/thumb/" + key;
    }
}
