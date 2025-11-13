package com.inninglog.inninglog.domain.post.dto.res;

import com.inninglog.inninglog.domain.contentImage.dto.res.ImageListResDto;
import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;
import com.inninglog.inninglog.domain.post.domain.Post;
import java.time.format.DateTimeFormatter;

public record PostSingleResDto(
        MemberShortResDto member,
        Long postId,
        String teamShortCode,
        String title,
        String content,
        long likeCount,
        long scrapCount,
        long commentCount,
        String postAt,
        boolean isEdit,
        ImageListResDto imageListResDto
) {
    public static PostSingleResDto of (Post post, MemberShortResDto memberShortResDto, ImageListResDto imageListResDto){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return new PostSingleResDto(
                memberShortResDto,
                post.getId(),
                post.getTeam_shortCode(),
                post.getTitle(),
                post.getContent(),
                post.getLikeCount(),
                post.getScrapCount(),
                post.getCommentCount(),
                post.getPostAt().format(formatter),
                post.isEdit(),
                imageListResDto
        );
    }
}
