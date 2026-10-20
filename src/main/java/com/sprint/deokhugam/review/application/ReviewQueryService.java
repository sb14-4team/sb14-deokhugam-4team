package com.sprint.deokhugam.review.application;

import com.sprint.deokhugam.review.application.provided.query.ReviewFinder;
import com.sprint.deokhugam.review.domain.Review;
import org.springframework.stereotype.Service;

@Service
public class ReviewQueryService implements ReviewFinder {

  @Override
  public Review findReview() {
    return null;
  }
}
