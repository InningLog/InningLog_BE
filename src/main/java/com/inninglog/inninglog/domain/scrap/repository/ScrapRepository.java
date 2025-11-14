package com.inninglog.inninglog.domain.scrap.repository;

import com.inninglog.inninglog.domain.scrap.domain.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
}
