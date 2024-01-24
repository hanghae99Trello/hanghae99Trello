package org.sparta.hanghae99trello.repository;

import org.sparta.hanghae99trello.entity.Board;
import org.sparta.hanghae99trello.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> findById(Long id);
    List<Board> findByCreatedBy(User user);
}
