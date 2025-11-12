package com.inninglog.inninglog.domain.post.repository;

import com.inninglog.inninglog.domain.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
