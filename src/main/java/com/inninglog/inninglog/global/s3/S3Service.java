package com.inninglog.inninglog.global.s3;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Uploader  s3Uploader;

    public String generateUrl(Long memebrId, String fileName, String contentType){

        if (fileName.contains("..") || fileName.contains("/")) {
            throw new CustomException(ErrorCode.INVALID_FILE_NAME);
        }

        String url = s3Uploader.generatePresignedUrl(
                "journal/" + memebrId +"/" +  fileName, contentType);

        return url;
    }
}
