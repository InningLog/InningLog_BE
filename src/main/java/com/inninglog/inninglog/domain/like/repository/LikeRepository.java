package com.inninglog.inninglog.domain.like.repository;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.like.domain.Like;
import com.inninglog.inninglog.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    //해당 콘텐츠에 좋아요 누른 여부
    boolean existsByContentTypeAndTargetIdAndMember(ContentType contentType, Long targetId, Member member);
}
