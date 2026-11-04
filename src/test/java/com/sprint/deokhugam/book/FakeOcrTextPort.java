package com.sprint.deokhugam.book;

import com.sprint.deokhugam.book.application.required.OcrTextPort;

// 테스트에서 OCR Space 대신 쓴다. setText로 넣어 둔 글을 그대로 돌려준다
public class FakeOcrTextPort implements OcrTextPort {

  private String text = "";

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String readText(byte[] image, String filename, String contentType) {
    return text;
  }
}
