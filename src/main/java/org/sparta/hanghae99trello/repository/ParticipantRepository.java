package org.sparta.hanghae99trello.repository;

import org.sparta.hanghae99trello.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant,Long> {

    Optional<Participant> findParticipantByBoardIdAndUserId(Long boardId, Long userId);
    List<Participant> findByParticipantNameInAndBoardId(List<String> operatorNames, Long boardId);
    void deleteByBoardId(Long id);
}
