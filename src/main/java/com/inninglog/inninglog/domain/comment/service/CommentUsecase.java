package com.inninglog.inninglog.domain.comment.service;

import com.inninglog.inninglog.domain.comment.dto.req.CommentCreateReqDto;
import com.inninglog.inninglog.domain.comment.dto.res.CommentListResDto;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.service.MemberGetService;
import com.inninglog.inninglog.domain.post.domain.Post;
import com.inninglog.inninglog.domain.post.service.PostCreateService;
import com.inninglog.inninglog.domain.post.service.PostValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentUsecase {

    private final PostValidateService postValidateService;
    private final PostCreateService postCreateService;
    private final CommentCreateService commentCreateService;
    private final CommentValidateServcie commentValidateServcie;
    private final CommentGetService commentGetService;

    //댓글 생성
    @Transactional
    public void createComment(ContentType contentType, CommentCreateReqDto dto, Long postId, Member member){
        Post post = postValidateService.getPostById(postId);
        if(dto.rootCommentId() != null) {
            commentValidateServcie.validateRootComment(dto.rootCommentId());
        }
        commentCreateService.createComment(contentType, dto, postId, member);
        postCreateService.increaseCommentCount(post);
    }

    //댓글 목록 조회
    @Transactional(readOnly = true)
    public CommentListResDto getComments(ContentType contentType, Long postId){
        postValidateService.getPostById(postId);
        return commentGetService.getCommentList(contentType,postId);
    }
}
