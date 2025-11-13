package com.inninglog.inninglog.global.s3;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class S3UrlProperties {

    private final String bucket;
    private final String region;
    private final String baseUrl;

    public S3UrlProperties(
            @Value("${cloud.aws.s3.bucket}") String bucket,
            @Value("${cloud.aws.s3.region}") String region
    ) {
        this.bucket = bucket;
        this.region = region;
        // S3 public URL 기본 조합
        this.baseUrl = String.format("https://%s.s3.%s.amazonaws.com", bucket, region);
    }
}
