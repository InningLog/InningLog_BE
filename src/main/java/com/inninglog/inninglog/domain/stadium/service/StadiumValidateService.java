package com.inninglog.inninglog.domain.stadium.service;

import com.inninglog.inninglog.domain.stadium.domain.Stadium;
import com.inninglog.inninglog.domain.stadium.repository.StadiumRepository;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StadiumValidateService {

    private final StadiumRepository stadiumRepository;

    @Transactional(readOnly = true)
    public Stadium validateStadium(String shortCode) {
        Stadium stadium = stadiumRepository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("⚠️존재하지 않는 구장: shortCode={}", shortCode);
                    return new CustomException(ErrorCode.STADIUM_NOT_FOUND);
                });

        return stadium;
    }
}
