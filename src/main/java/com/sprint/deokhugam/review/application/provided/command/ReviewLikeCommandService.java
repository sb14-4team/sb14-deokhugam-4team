package com.sprint.deokhugam.review.application.provided.command;

import com.sprint.deokhugam.review.application.required.ReviewLikeRepositoryPort;
import com.sprint.deokhugam.review.application.required.ReviewRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewLikeCommandService implements ReviewLikeCommand {
  private final ReviewLikeRepositoryPort reviewLikeRepositoryPort;
  private final ReviewRepositoryPort reviewRepositoryPort;

  /*
   일괄 카운트 감소 -> 일괄 좋아요 삭제로 진행
     - 이미 삭제(소프트딜리트)되었거나 삭제로직이 끝나고 남을 리뷰에만 업데이트를 반영하는 로직 추가는 안함
     호출부에서 유효한 유저아이디를 던진다
  */
  @Override
  @Transactional
  public long deleteLikesByUserId(UUID userId) {
    try {
      reviewRepositoryPort.decreaseLikeCountsByUserId(userId); // 좋아요 수 감소 반영
      return reviewLikeRepositoryPort.deleteAllByUserId(userId);
    } catch (RuntimeException e) {
      log.error("탈퇴 사용자의 좋아요 정리 실패 -- userID = {}", userId, e);
      throw e;
    }
  }
}
