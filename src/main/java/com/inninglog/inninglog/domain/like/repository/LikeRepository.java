package com.inninglog.inninglog.domain.like.repository;

import com.inninglog.inninglog.domain.like.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
}
