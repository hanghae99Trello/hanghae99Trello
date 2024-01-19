package org.sparta.hanghae99trello.repository;

import org.sparta.hanghae99trello.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant,Long> {
    List<Participant> findAllByBoardId(Long boardId);
    Participant findByBoardIdAndUserId(Long boardId, Long userId);
    void deleteByBoardId(Long id);
}
