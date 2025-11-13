package com.inninglog.inninglog.domain.comment.dto;

public record CommentCreateReqDto(
        Long rootCommentId,
        String content
) {
}
