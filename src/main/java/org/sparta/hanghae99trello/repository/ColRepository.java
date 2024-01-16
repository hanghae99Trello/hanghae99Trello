package org.sparta.hanghae99trello.repository;

import org.sparta.hanghae99trello.entity.Col;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ColRepository extends JpaRepository<Col, Long> {
    List<Col> findByBoardId(Long boardId);
    Optional<Col> findById(Long id);

}
