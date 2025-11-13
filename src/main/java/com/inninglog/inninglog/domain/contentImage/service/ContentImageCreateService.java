package com.inninglog.inninglog.domain.contentImage.service;

import com.inninglog.inninglog.domain.contentImage.dto.req.ImageUploadReqDto;
import com.inninglog.inninglog.domain.contentImage.dto.res.ImageUploadResDto;
import com.inninglog.inninglog.global.s3.PreSginedPutService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentImageCreateService {

    private final PreSginedPutService preSginedPutService;

    public List<ImageUploadResDto> getPostImagePresignedUrl(
            List<ImageUploadReqDto> dtos,
            Long memberId
    ) {

        List<ImageUploadResDto> result = new ArrayList<>();

        for (ImageUploadReqDto dto : dtos) {
            String url = putPostImage(dto, memberId);

            result.add(
                    new ImageUploadResDto(
                            dto.sequence(),
                            url
                    )
            );
        }

        return result;
    }

    private String putPostImage(ImageUploadReqDto dto, Long memberId) {
        return preSginedPutService.postPutPreUrl(
                memberId,
                dto.fileName(),
                dto.contentType()
        );
    }
}