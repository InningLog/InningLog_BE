package com.inninglog.inninglog.domain.comment.service;

import com.inninglog.inninglog.domain.comment.domain.Comment;
import com.inninglog.inninglog.domain.comment.dto.CommentCreateReqDto;
import com.inninglog.inninglog.domain.comment.repository.CommentRepository;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentCreateService {

    private final CommentRepository commentRepository;

    //댓글 생성
    public void createPostComment(CommentCreateReqDto dto, Long postId, Member member){
        Comment comment = Comment.of(dto, ContentType.POST, postId, member);
        commentRepository.save(comment);
    }
}
