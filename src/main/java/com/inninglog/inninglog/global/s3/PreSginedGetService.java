package com.inninglog.inninglog.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreSginedGetService {
    private final S3Uploader s3Uploader;

    public String journalGetPreUrl(String key){
        return s3Uploader.generatePresignedGetUrl(key);
    }

    public String SeatViewGetPreUrl(String key){
        return s3Uploader.generatePresignedGetUrl(key);
    }
}
