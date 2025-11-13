package com.inninglog.inninglog.domain.post.service;

import com.inninglog.inninglog.domain.contentImage.dto.res.ImageListResDto;
import com.inninglog.inninglog.domain.contentImage.service.ImageGetService;
import com.inninglog.inninglog.domain.contentImage.service.PostImageCreateService;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;
import com.inninglog.inninglog.domain.member.service.MemberGetService;
import com.inninglog.inninglog.domain.post.domain.Post;
import com.inninglog.inninglog.domain.post.dto.req.PostCreateReqDto;
import com.inninglog.inninglog.domain.post.dto.res.PostSingleResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostUsecase {

    private final PostCreateService postCreateService;
    private final PostImageCreateService postImageCreateService;
    private final PostGetService postGetService;
    private final PostValidateService postValidateService;
    private final MemberGetService memberGetService;
    private final ImageGetService imageGetService;

    //게시글 업로드 + 이미지 저장
    @Transactional
    public void uploadPost (PostCreateReqDto dto,String team_Shortcode, Member member){
        Long postId = postCreateService.createPost(dto,team_Shortcode,member);
        postImageCreateService.createPostImageList(postId, dto.imageCreateReqDto());
    }

    //게시글 단일 조회
    public PostSingleResDto getSinglePost(ContentType contentType, Long postId){
        Post post = postValidateService.getPostById(postId);
        MemberShortResDto memberShortResDto = memberGetService.toMemberShortResDto(post.getMember());
        ImageListResDto imageListResDto = imageGetService.getImageList(contentType, postId);
        return postGetService.getSinglePost(post, memberShortResDto, imageListResDto);
    }
}
