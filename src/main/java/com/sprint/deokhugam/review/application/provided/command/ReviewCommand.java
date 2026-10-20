package com.sprint.deokhugam.review.application.provided.command;

import com.sprint.deokhugam.review.application.dto.CreateReviewCommand;
import com.sprint.deokhugam.review.domain.Review;
import java.util.UUID;

/*
 포트 : 인터페이스

 인바운드 어댑터인 컨트롤러가 포트인 프로바이디드를 가져옴
 기능의 인터페이스
 변화 주는
*/
public interface ReviewCommand {
  Review create(CreateReviewCommand command);

  void deleteReview(UUID reviewId);
}
