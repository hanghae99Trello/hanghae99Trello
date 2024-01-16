package org.sparta.hanghae99trello.service;

import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.dto.CommentResponseDto;
import org.sparta.hanghae99trello.entity.Card;
import org.sparta.hanghae99trello.entity.Comment;
import org.sparta.hanghae99trello.entity.User;
import org.sparta.hanghae99trello.repository.CardRepository;
import org.sparta.hanghae99trello.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CardRepository cardRepository;

    //TODO :: 유저 정보 반드시 필요함
    @Transactional
    public CommentResponseDto createComment(User user, Long cardId, String commentMessage) {

        Card card = cardRepository.findById(cardId).orElseThrow(
                ()-> new IllegalArgumentException("해당하는 카드가 없습니다.")
        );

        Comment comment = new Comment(user, card, commentMessage);
        commentRepository.save(comment);

        card.addComment(comment);

        return new CommentResponseDto(comment);
    }
}
