package com.sprint.deokhugam.book.application.required;

/** 이미지에 찍힌 글자를 읽어 오는 포트. 지금 구현은 OCR Space 어댑터 하나다. */
public interface OcrTextPort {

  /** 읽은 글자를 그대로 돌려준다. 글자가 없으면 빈 문자열. */
  String readText(byte[] image, String filename, String contentType);
}
