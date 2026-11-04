package com.sprint.deokhugam.dashboard.review.adapter.persistence;

import static com.sprint.deokhugam.dashboard.review.domain.QPopularReview.popularReview;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.deokhugam.dashboard.review.application.required.PopularReviewPersistencePort;
import com.sprint.deokhugam.dashboard.review.domain.PopularReview;
import com.sprint.deokhugam.dashboard.review.domain.PopularReviewPeriod;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
// PopularReviews 데이터 갱신
public class PopularReviewJpaPersistencePortAdapter implements PopularReviewPersistencePort {

  private final JPAQueryFactory queryFactory;
  private final PopularReviewJpaRepository jpaRepository;

  @Override
  public void replaceAll(PopularReviewPeriod period, List<PopularReview> popularReviews) {
    deleteAllByPeriod(period);
    jpaRepository.saveAll(popularReviews);
  }

  // 해당 기간의 기존 순위를 한 번의 delete 쿼리로 지운다
  private void deleteAllByPeriod(PopularReviewPeriod period) {
    queryFactory.delete(popularReview).where(popularReview.period.eq(period)).execute();
  }
}
