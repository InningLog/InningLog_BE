package com.inninglog.inninglog.domain.contentImage.service;

import com.inninglog.inninglog.domain.contentImage.dto.req.ImageUploadReqDto;
import com.inninglog.inninglog.global.s3.PreSginedPutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentImageCreateService {

    PreSginedPutService preSginedPutService;

    public String putPostImage(ImageUploadReqDto dto, Long memberId){
        return preSginedPutService.postPutPreUrl(memberId, dto.fileName(), dto.contentType());
    }
}
