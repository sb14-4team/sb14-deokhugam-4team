package com.sprint.deokhugam.book.domain;

// 썸네일 파일이 바뀌었다는 사실. 등록은 newKey만, 수정은 둘 다, 삭제는 previousKey만 있다
public record ThumbnailChangedEvent(String newKey, String previousKey) {}
