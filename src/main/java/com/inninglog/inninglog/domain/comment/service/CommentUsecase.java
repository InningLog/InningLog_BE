package com.inninglog.inninglog.domain.comment.service;

import com.inninglog.inninglog.domain.comment.dto.req.CommentCreateReqDto;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.post.service.PostValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentUsecase {

    private final PostValidateService postValidateService;
    private final CommentCreateService commentCreateService;
    private final CommentValidateServcie commentValidateServcie;

    //게시글 생성
    @Transactional
    public void createComment(ContentType contentType, CommentCreateReqDto dto, Long postId, Member member){
        postValidateService.validatePost(postId);
        commentValidateServcie.validateRootComment(dto.rootCommentId());
        commentCreateService.createComment(contentType, dto, postId, member);
    }
}
