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

    //TODO:: 유저 권한 확인 필요
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
        cardRepository.save(card);
        Long result = col.addCard(card);

         if(!result.equals(card.getId())){
            Card prevCard = cardRepository.findById(result).orElseThrow(
                    ()->new IllegalArgumentException("해당하는 카드가 없습니다.")
            );
             prevCard.setNextCardId(card.getId());
            card.setPreviousCardId(prevCard.getId());
        }

        return new CardResponseDto(card);
    }

    @Transactional
    public CardResponseDto updateCardColumn(Long cardId, Long from, Long to) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new IllegalArgumentException("아이디에 해당하는 카드가 없습니다.")
        );

        Card prevCard = card.getPreviousCardId() != null
                ? cardRepository.findById(card.getPreviousCardId()).orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 카드가 없습니다."))
                : null;
        Card nextCard = card.getNextCardId() != null
                ? cardRepository.findById(card.getNextCardId()).orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 카드가 없습니다."))
                : null;

        //delete
        Col fromCol = colRepository.findById(from).orElseThrow(
                () -> new IllegalArgumentException("아이디에 해당하는 컬럼이 없습니다.")
        );

        Boolean isEnd = fromCol.deleteCard(card);

        if (!isEnd){
            if(prevCard != null && nextCard != null) {
                prevCard.setNextCardId(nextCard.getId());
                nextCard.setPreviousCardId(prevCard.getId());
            } else if(prevCard != null) {
                prevCard.setNextCardId(null);
            } else if(nextCard != null) {
                nextCard.setPreviousCardId(null);
            }
        }

        //연관관계 제거
        card.setPreviousCardId(null);
        card.setNextCardId(null);

        //새로 이동
        Col toCol = colRepository.findById(to).orElseThrow(
                ()->new IllegalArgumentException("해당하는 컬럼이 없습니다.")
        );

        Long result = toCol.addCard(card);

        if(!result.equals(card.getId())){
            Card prevCard2 = cardRepository.findById(result).orElseThrow(
                    ()->new IllegalArgumentException("해당하는 카드가 없습니다.")
            );
            prevCard2.setNextCardId(card.getId());
            card.setPreviousCardId(prevCard2.getId());
        }
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
                () -> new IllegalArgumentException("아이디에 해당하는 컬럼이 없습니다.")
        );
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new IllegalArgumentException("아이디에 해당하는 카드가 없습니다.")
        );

        Boolean isEnd = col.deleteCard(card);

        Card prevCard = card.getPreviousCardId() != null
                ? cardRepository.findById(card.getPreviousCardId()).orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 카드가 없습니다."))
                : null;
        Card nextCard = card.getNextCardId() != null
                ? cardRepository.findById(card.getNextCardId()).orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 카드가 없습니다."))
                : null;

        //둘 중 하나라도 없지 않은 경우
        if (!isEnd){
            if(prevCard != null && nextCard != null) {
                prevCard.setNextCardId(nextCard.getId());
                nextCard.setPreviousCardId(prevCard.getId());
            } else if(prevCard != null) {
                prevCard.setNextCardId(null);
            } else if(nextCard != null) {
                nextCard.setPreviousCardId(null);
            }
        }
        cardRepository.delete(card);
    }



    //TODO::Column에 대한 처리 필요 -> 같은 Col 일때만 가능하도록? Col에서 처리하지 않고 여기서 처리해야됨(column 참조를 늘리던가)
    @Transactional
    public void updateCardOrder(Long cardId, Long cardOrderIndex) {

        //여기서 문제 카드레포지토리를 뒤질게 아니라 column에서 순차적 서치를 해야됨. 아니면 다른 컬럼에서 조회할 수 있음


        Card prevCard = cardRepository.findById(cardId).orElseThrow(
                ()-> new IllegalArgumentException("아이디에 해당하는 카드가 없습니다.")
        );

        Card nextCard = cardRepository.findById(cardOrderIndex).orElseThrow(
                ()-> new IllegalArgumentException("아이디에 해당하는 카드가 없습니다.")
        );


        Long prevCardPrevId = prevCard.getPreviousCardId();
        Long prevCardNextId = prevCard.getNextCardId();

        Long nextCardPrevId = nextCard.getPreviousCardId();
        Long nextCardNextId = nextCard.getNextCardId();

        prevCard.setPreviousCardId(nextCardPrevId);
        prevCard.setNextCardId(nextCardNextId);

        nextCard.setPreviousCardId(prevCardPrevId);
        nextCard.setNextCardId(prevCardNextId);

        if (prevCardPrevId != null){
            Card prevCardPrev = cardRepository.findById(prevCardPrevId).orElseThrow(
                    ()-> new IllegalArgumentException("아이디에 해당하는 카드가 없습니다.")
            );
            prevCardPrev.setNextCardId(nextCard.getId());
        }

        if (prevCardNextId != null){
            Card prevCardNext = cardRepository.findById(prevCardNextId).orElseThrow(
                    ()-> new IllegalArgumentException("아이디에 해당하는 카드가 없습니다.")
            );
            prevCardNext.setPreviousCardId(nextCard.getId());
        }

        if (nextCardPrevId != null){
            Card nextCardPrev = cardRepository.findById(nextCardPrevId).orElseThrow(
                    ()-> new IllegalArgumentException("아이디에 해당하는 카드가 없습니다.")
            );
            nextCardPrev.setNextCardId(prevCard.getId());
        }

        if (nextCardNextId != null){
            Card nextCardNext = cardRepository.findById(nextCardNextId).orElseThrow(
                    ()-> new IllegalArgumentException("아이디에 해당하는 카드가 없습니다.")
            );
            nextCardNext.setPreviousCardId(prevCard.getId());
        }

    }

}
