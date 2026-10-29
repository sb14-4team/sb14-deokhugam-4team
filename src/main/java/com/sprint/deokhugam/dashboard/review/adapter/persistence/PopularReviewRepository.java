package com.sprint.deokhugam.dashboard.review.adapter.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PopularReviewRepository {

  private final JPAQueryFactory jpaQueryFactory;
}
