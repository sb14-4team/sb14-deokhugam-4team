package com.sprint.deokhugam.dashboard.book.application.required;

import com.sprint.deokhugam.dashboard.book.application.provided.query.PopularBookSearchCondition;
import com.sprint.deokhugam.dashboard.book.domain.PopularBook;
import java.util.List;

public interface PopularBookQueryPort {
  List<PopularBook> search(PopularBookSearchCondition condition);
}
