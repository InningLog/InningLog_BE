package com.inninglog.inninglog.domain.comment.dto;

import jakarta.annotation.Nullable;

public record CommentCreateReqDto(
        @Nullable
        Long rootCommentId,

        String content
) {
}
