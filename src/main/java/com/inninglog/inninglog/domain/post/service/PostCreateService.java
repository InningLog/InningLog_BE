package com.inninglog.inninglog.domain.post.service;

import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.post.domain.Post;
import com.inninglog.inninglog.domain.post.dto.req.PostCreateReqDto;
import com.inninglog.inninglog.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCreateService {

    private final PostRepository postRepository;

    //게시글 생성
    public void createPost(PostCreateReqDto dto,String team_shortcode, Member member){
        Post post = Post.of(dto, team_shortcode, member);
        postRepository.save(post);
    }
}
