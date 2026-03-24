package com.inninglog.inninglog.domain.contentImage.service;

import com.inninglog.inninglog.domain.contentImage.dto.req.ImageUploadReqDto;
import com.inninglog.inninglog.domain.contentImage.dto.res.ImageListUploadResDto;
import com.inninglog.inninglog.domain.contentImage.dto.res.ImageUploadResDto;
import com.inninglog.inninglog.global.s3.PreSginedPutService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatViewImageUploadService {

    private final PreSginedPutService preSginedPutService;

    public ImageListUploadResDto getSeatViewImagePresignedUrlList(List<ImageUploadReqDto> dtos, Long memberId) {
        List<ImageUploadResDto> result = new ArrayList<>();

        for (ImageUploadReqDto dto : dtos) {
            String url = preSginedPutService.seatViewPutPreUrl(memberId, dto.fileName(), dto.contentType());
            String key = "seatView/" + memberId + "/" + dto.fileName();

            result.add(new ImageUploadResDto(dto.sequence(), url, key));
        }

        return ImageListUploadResDto.of(result);
    }
}
