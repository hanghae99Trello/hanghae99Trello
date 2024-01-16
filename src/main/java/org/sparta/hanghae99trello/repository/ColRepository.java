package org.sparta.hanghae99trello.repository;

import org.sparta.hanghae99trello.entity.Col;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface ColRepository extends JpaRepository<Col, Long> {
    List<Col> findByBoardId(Long boardId);
    Optional<Col> findById(Long id);

}
