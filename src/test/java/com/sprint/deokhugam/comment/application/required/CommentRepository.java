package com.sprint.deokhugam.comment.application.required;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CommentRepository {

  @Autowired CommentRepository commentRepository;
}
