package org.sparta.hanghae99trello.service;

import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.dto.CommentResponseDto;
import org.sparta.hanghae99trello.entity.Card;
import org.sparta.hanghae99trello.entity.Comment;
import org.sparta.hanghae99trello.entity.Participant;
import org.sparta.hanghae99trello.repository.CardRepository;
import org.sparta.hanghae99trello.repository.CommentRepository;
import org.sparta.hanghae99trello.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CardRepository cardRepository;
    private final ParticipantRepository participantRepository;
    @Transactional
    public CommentResponseDto createComment(Long userId, Long boardId, Long cardId, String commentMessage) {
        Participant participant = participantRepository.findByBoardIdAndUserId(boardId,userId);
        if (participant == null){
            throw new IllegalArgumentException("보드와 아이디에 해당하는 참여자가 없습니다.");
        }
        Card card = cardRepository.findById(cardId).orElseThrow(
                ()-> new IllegalArgumentException("해당하는 카드가 없습니다.")
        );

        Comment comment = new Comment(participant, card, commentMessage);
        commentRepository.save(comment);
        card.addComment(comment);

        return new CommentResponseDto(comment);
    }
}
