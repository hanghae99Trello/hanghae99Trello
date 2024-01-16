package org.sparta.hanghae99trello.repository;

import org.sparta.hanghae99trello.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByBoardName(String boardName);
    Optional<Board> findById(Long id);
}
