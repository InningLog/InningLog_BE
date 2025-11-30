package com.inninglog.inninglog.domain.post.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "게시글 목록")
public record PostListResDto(
        @Schema(description = "각각의 게시글")
    List<PostSingleResDto> postSingleResDto
) {
    public static PostListResDto of (List<PostSingleResDto> postSingleResDto) {
        return new PostListResDto(postSingleResDto);
    }
}
