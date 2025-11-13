package com.inninglog.inninglog.domain.post.service;

import com.inninglog.inninglog.domain.contentImage.service.PostImageCreateService;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.post.dto.req.PostCreateReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostUsecase {

    private final PostCreateService postCreateService;
    private final PostImageCreateService postImageCreateService;

    //게시글 업로드 + 이미지 저장
    @Transactional
    public void uploadPost (PostCreateReqDto dto,String team_Shortcode, Member member){
        Long postId = postCreateService.createPost(dto,team_Shortcode,member);
        postImageCreateService.createPostImageList(postId, dto.imageCreateReqDto());
    }
}
