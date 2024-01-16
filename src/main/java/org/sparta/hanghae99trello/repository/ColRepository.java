package org.sparta.hanghae99trello.repository;

import org.sparta.hanghae99trello.entity.Col;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColRepository extends JpaRepository<Col, Long> {
}
