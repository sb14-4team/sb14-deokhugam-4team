package com.sprint.deokhugam.book.domain;

// 도서가 물리 삭제됐다는 사실. 썸네일이 없던 도서는 thumbnailKey가 null
public record BookDeletedEvent(String thumbnailKey) {}
