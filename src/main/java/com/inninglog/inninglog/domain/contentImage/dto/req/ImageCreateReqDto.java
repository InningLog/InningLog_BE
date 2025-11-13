package com.inninglog.inninglog.domain.contentImage.dto.req;

import jakarta.persistence.criteria.CriteriaBuilder.In;

public record ImageCreateReqDto(
        Integer sequence,
        String key
) {
}
