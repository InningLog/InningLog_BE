package com.inninglog.inninglog.domain.post.service;

import com.inninglog.inninglog.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCreateService {

    private final PostRepository postRepository;

    //게시글 생성
}
