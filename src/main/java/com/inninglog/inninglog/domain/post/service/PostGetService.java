package com.inninglog.inninglog.domain.post.service;

import com.inninglog.inninglog.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostGetService {

    private final PostRepository postRepository;

}
