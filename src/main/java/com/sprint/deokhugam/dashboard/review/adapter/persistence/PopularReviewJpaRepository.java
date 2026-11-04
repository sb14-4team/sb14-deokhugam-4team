package com.sprint.deokhugam.dashboard.review.adapter.persistence;

import com.sprint.deokhugam.dashboard.review.domain.PopularReview;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

// 저장(saveAll)만 담당. 조회·삭제 쿼리는 Querydsl로 작성
public interface PopularReviewJpaRepository extends JpaRepository<PopularReview, UUID> {}
