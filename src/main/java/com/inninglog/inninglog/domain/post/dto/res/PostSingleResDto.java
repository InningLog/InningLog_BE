package com.inninglog.inninglog.domain.post.dto.res;

import com.inninglog.inninglog.domain.contentImage.dto.res.ImageListResDto;
import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;

public record PostSingleResDto(
        MemberShortResDto member,
        Long postId,
        String title,
        String content,
        long likeCount,
        long scrapCount,
        long commentCount,
        String postAt,
        boolean isEdit,
        ImageListResDto imageListResDto
) {
}
