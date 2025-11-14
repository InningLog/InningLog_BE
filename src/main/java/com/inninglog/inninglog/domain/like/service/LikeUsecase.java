package com.inninglog.inninglog.domain.like.service;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.like.domain.LikeableContent;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.post.service.PostUpdateService;
import com.inninglog.inninglog.domain.post.service.PostValidateService;
import com.inninglog.inninglog.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeUsecase {

    private final LikeCreateService likeCreateService;
    private final PostValidateService postValidateService;
    private final PostUpdateService postUpdateService;

    //좋아요 생성
    @Transactional
    public void createLike(ContentType contentType, Long targetId, Member member) {
        LikeableContent content = validateContent(contentType, targetId);
        likeCreateService.createLikeAtPost(contentType, targetId, member);
        content.increaseLikeCount();
    }
    
    //콘텐츠 검증
    @Transactional(readOnly = true)
    protected LikeableContent validateContent(ContentType contentType, Long targetId){
        if(contentType==ContentType.POST){
            return postValidateService.getPostById(targetId);
        } else if (contentType==ContentType.JOURNAL) {
            //직관일지 반환
        } else if (contentType==ContentType.MARKET) {
            //이닝장터 반환
        }
        throw new IllegalArgumentException("지원 안 함");
    }
}
