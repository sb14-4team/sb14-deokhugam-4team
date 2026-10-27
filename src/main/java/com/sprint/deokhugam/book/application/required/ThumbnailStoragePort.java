package com.sprint.deokhugam.book.application.required;

// 썸네일 파일을 올리고 지우는 포트
public interface ThumbnailStoragePort {

  // 올린 파일의 공개 URL을 돌려준다. 실패하면 THUMBNAIL_UPLOAD_FAILED
  String upload(String key, byte[] bytes, String contentType);

  // 실패해도 예외 없이 로그만 남긴다
  void delete(String key);
}
