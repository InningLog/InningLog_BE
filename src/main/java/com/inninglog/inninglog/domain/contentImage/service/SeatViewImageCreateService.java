package com.inninglog.inninglog.domain.contentImage.service;

import com.inninglog.inninglog.domain.contentImage.domain.ContentImage;
import com.inninglog.inninglog.domain.contentImage.repository.ContentImageRepository;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.global.s3.S3UrlProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatViewImageCreateService {

    private final ContentImageRepository contentImageRepository;
    private final S3UrlProperties s3UrlProperties;

    @Transactional
    public void createSeatViewImages(Long seatViewId, List<String> fileNames, Long memberId) {
        if (fileNames == null || fileNames.isEmpty()) {
            return;
        }

        for (int i = 0; i < fileNames.size(); i++) {
            String fileName = fileNames.get(i);
            if (fileName == null || fileName.trim().isEmpty()) {
                continue;
            }

            String key = "seatView/" + memberId + "/" + fileName;
            String originalUrl = s3UrlProperties.getBaseUrl() + "/" + key;

            ContentImage contentImage = ContentImage.builder()
                    .contentType(ContentType.SEATVIEW)
                    .targetId(seatViewId)
                    .originalUrl(originalUrl)
                    .sequence(i + 1)
                    .build();

            contentImageRepository.save(contentImage);
        }
    }
}
