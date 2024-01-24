package org.sparta.hanghae99trello.repository;

import org.sparta.hanghae99trello.entity.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperatorRepository extends JpaRepository<Operator,Long> {

}
