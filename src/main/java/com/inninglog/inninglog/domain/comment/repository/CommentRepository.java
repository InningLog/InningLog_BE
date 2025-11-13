package com.inninglog.inninglog.domain.comment.repository;

import com.inninglog.inninglog.domain.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
