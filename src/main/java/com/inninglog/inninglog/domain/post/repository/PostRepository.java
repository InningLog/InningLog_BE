package com.inninglog.inninglog.domain.post.repository;

import com.inninglog.inninglog.domain.post.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Slice<Post> findByTeamShortCodeOrderByPostAtDesc(String teamShortCode, Pageable pageable);

    // N+1 최적화: Member를 한 번에 조회
    @Query("SELECT p FROM Post p JOIN FETCH p.member WHERE p.teamShortCode = :teamShortCode ORDER BY p.postAt DESC")
    Slice<Post> findWithMemberByTeamShortCode(String teamShortCode, Pageable pageable);
}