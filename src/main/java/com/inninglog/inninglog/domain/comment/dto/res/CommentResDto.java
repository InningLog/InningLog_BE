package com.inninglog.inninglog.domain.comment.dto.res;

import com.inninglog.inninglog.domain.comment.domain.Comment;
import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;
import java.time.format.DateTimeFormatter;

public record CommentResDto (
        long commentId,
        MemberShortResDto memberShortResDto,
        long rootCommentId,
        String content,
        String commentAt,
        long likeCount,
        boolean isDeleted
        ){
    public static CommentResDto of (Comment comment, MemberShortResDto memberShortResDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return new CommentResDto(
                comment.getId(),
                memberShortResDto,
                comment.getRootCommentId(),
                comment.getContent(),
                comment.getCommentAt().format(formatter),
                comment.getLikeCount(),
                comment.isDeleted()
        );
    }
}
