package com.sprint.deokhugam.user.adapter.persistence;

import static com.sprint.deokhugam.user.domain.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.deokhugam.user.application.required.UserRepository;
import com.sprint.deokhugam.user.domain.Email;
import com.sprint.deokhugam.user.domain.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

  private final UserJpaRepository userJpaRepository;
  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public User save(User user) {
    return userJpaRepository.save(user);
  }

  @Override
  public boolean existsByEmail(Email email) {
    return userJpaRepository.existsByEmail(email);
  }

  @Override
  public Optional<User> findByEmail(Email email) {
    return userJpaRepository.findByEmail(email);
  }

  @Override
  public Optional<User> findById(UUID userId) {
    return userJpaRepository.findById(userId);
  }

  /*
   * 물리 삭제시 한 번에 전부 삭제할지 나눠서 할지 논의해봐야할듯.
   * */
  @Override
  public List<UUID> findCleanupIds(Instant cutoff, int limit) {
    return jpaQueryFactory
        .select(user.id)
        .from(user)
        .where(user.deletedAt.isNotNull().and(user.deletedAt.loe(cutoff)))
        .orderBy(user.deletedAt.asc(), user.id.asc())
        .limit(limit)
        .fetch();
  }

  @Override
  public void remove(UUID userId) {
    userJpaRepository.deleteById(userId);
  }
}
