package com.sprint.deokhugam.dashboard.review.application;

import com.sprint.deokhugam.dashboard.review.adapter.persistence.PopularReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopularReviewService {

  private final PopularReviewRepository popularReviewRepository;

  public void aggregateAndSavePopularReviews() {}
}
