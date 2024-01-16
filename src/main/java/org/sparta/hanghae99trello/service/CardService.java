package org.sparta.hanghae99trello.service;


import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.dto.CardResponseDto;
import org.sparta.hanghae99trello.entity.Card;
import org.sparta.hanghae99trello.entity.Col;
import org.sparta.hanghae99trello.entity.Participant;
import org.sparta.hanghae99trello.entity.User;
import org.sparta.hanghae99trello.repository.CardRepository;
import org.sparta.hanghae99trello.repository.ColRepository;
import org.sparta.hanghae99trello.repository.ParticipantRepository;
import org.sparta.hanghae99trello.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final ColRepository colRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;

    //TODO:: 유저 권한 확인 필요!, 정렬 순서 확인 필요
    @Transactional
    public CardResponseDto createCard(Long boardId, Long columnId, String cardName,
                                      String cardDescription, String color, List<Long> operatorIds) {

        List<User> operators = userRepository.findByIdIn(operatorIds);
        List<User> participants = participantRepository.findAllByBoardId(boardId).stream().map(Participant::getUser).toList();

        for (User operator : operators) {
            if (!participants.contains(operator)) {
                throw new IllegalArgumentException("Board에 속하지 않은 유저입니다.");
            }
        }
        Col col = colRepository.findById(columnId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 컬럼이 없습니다")
        );

        Card card = new Card(cardName, cardDescription, color, operators);
        col.addCard(card);
        cardRepository.save(card);
        return new CardResponseDto(card);
    }

    @Transactional
    public CardResponseDto getCard(Long boardId, Long columnId, Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new IllegalArgumentException("아이디에 해당하는 카드가 없습니다.")
        );
        return new CardResponseDto(card);
    }

    @Transactional
    public CardResponseDto updateCard(Long cardId, String cardName, String cardDescription, String color, List<Long> operatorIds, LocalDate dueDate) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new IllegalArgumentException("아이디에 해당하는 카드가 없습니다.")
        );
        List<User> operators = userRepository.findByIdIn(operatorIds);
        card.update(cardName, cardDescription, color, operators, dueDate);
        return new CardResponseDto(card);
    }

    @Transactional
    public void deleteCard(Long columnId, Long cardId) {
        Col col = colRepository.findById(columnId).orElseThrow(
                ()-> new IllegalArgumentException("아이디에 해당하는 컬럼이 없습니다.")
        );
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new IllegalArgumentException("아이디에 해당하는 카드가 없습니다.")
        );

        col.deleteCard(card);
        cardRepository.delete(card);

    }

    @Transactional
    public CardResponseDto updateCardColumn(Long cardId, Long from, Long to) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 카드를 찾을 수 없습니다.")
        );

        Col fromCol = colRepository.findById(from).orElseThrow(
                () -> new IllegalArgumentException("해당하는 컬럼을 찾을 수 없습니다.")
        );

        Col toCol = colRepository.findById(to).orElseThrow(
                () -> new IllegalArgumentException("해당하는 컬럼을 찾을 수 없습니다.")
        );

        fromCol.deleteCard(card);
        toCol.addCard(card);

        return new CardResponseDto(card);
    }
}
