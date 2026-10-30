package com.sprint.deokhugam.review.application.provided.command;

import java.util.List;
import java.util.UUID;

public interface ReviewCommand {
  /*
   배치 스케줄러에서 호출하는 메서드
   요청인자 : 삭제할 리뷰 아이디 목록
   리턴값 : long 삭제 완료된 레코드 수 - 쿼리 dsl delete 리턴값 long 이라 long 으로 리턴
   - 주의 : 연관된 댓글/좋아요가 먼저 정리되어 있어야 함
  */
  long deleteReviewByIds(List<UUID> reviewIds);
}
