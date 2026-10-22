package com.sprint.deokhugam.book.adapter.web;

import com.sprint.deokhugam.book.application.dto.BookCreateRequest;
import com.sprint.deokhugam.book.application.dto.BookDto;
import com.sprint.deokhugam.book.application.provided.command.BookCommand;
import com.sprint.deokhugam.book.application.provided.query.BookFinder;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

  private final BookCommand bookCommand;
  private final BookFinder bookFinder;

  // thumbnailImage는 명세에 맞춰 받기만 한다. S3 저장은 A06에서 연결
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BookDto> create(
      @RequestPart("bookData") @Valid BookCreateRequest bookData,
      @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage) {
    BookDto bookDto = bookCommand.create(bookData);
    return ResponseEntity.status(HttpStatus.CREATED).body(bookDto);
  }

  @GetMapping("/{bookId}")
  public ResponseEntity<BookDto> get(@PathVariable UUID bookId) {
    return ResponseEntity.ok(bookFinder.getById(bookId));
  }
}
