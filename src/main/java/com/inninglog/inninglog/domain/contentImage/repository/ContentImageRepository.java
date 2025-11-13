package com.inninglog.inninglog.domain.contentImage.repository;

import com.inninglog.inninglog.domain.contentImage.domain.ContentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentImageRepository extends JpaRepository<ContentImage, Long> {
}
