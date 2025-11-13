package com.inninglog.inninglog.domain.comment.service;

import com.inninglog.inninglog.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentGetService {
    private final CommentRepository commentRepository;

    //댓글 조회
}
