package org.sparta.hanghae99trello.repository;

import org.sparta.hanghae99trello.entity.Card;
import org.sparta.hanghae99trello.entity.Col;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card,Long> {
    List<Card> findAllByColIdOrderByOrderIndexAsc(Long colId);
}
