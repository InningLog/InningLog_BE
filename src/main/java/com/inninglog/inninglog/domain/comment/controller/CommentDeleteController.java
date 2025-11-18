package com.inninglog.inninglog.domain.comment.controller;

import com.inninglog.inninglog.domain.comment.domain.Comment;
import com.inninglog.inninglog.domain.comment.service.CommentDeleteService;
import com.inninglog.inninglog.domain.comment.service.CommentUsecase;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "댓글", description = "댓글 관련 API")
public class CommentDeleteController {

    private final CommentUsecase commentUsecase;

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<SuccessResponse<Void>> deleteComments(@PathVariable("commentId") long commentId){
        commentUsecase.deleteComment(commentId);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}
