package com.inninglog.inninglog.domain.contentImage.service;

import com.inninglog.inninglog.domain.contentImage.domain.ContentImage;
import com.inninglog.inninglog.domain.contentImage.dto.req.ImageUploadReqDto;
import com.inninglog.inninglog.domain.contentType.ContentType;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostImageCreateService {

    @Transactional
    public void createPostImage(Long postId, ImageUploadReqDto dto) {
        ContentImage contentImage = ContentImage.of(ContentType.POST, postId, originalUrl, dto);

    }
}
