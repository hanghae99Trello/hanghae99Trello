package org.sparta.hanghae99trello.service;


import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.dto.CardResponseDto;
import org.sparta.hanghae99trello.entity.*;
import org.sparta.hanghae99trello.repository.*;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//TODO:: 유저 권한 확인 필요
@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final ColRepository colRepository;
    private final ParticipantRepository participantRepository;
    private final CommentRepository commentRepository;
    private final OperatorRepository operatorRepository;
    @Transactional
    public CardResponseDto createCard(Long boardId, Long columnId, String cardName,
                                      String cardDescription, String color, List<Long> operatorIds) {

        Card card = new Card(cardName, cardDescription, color);
        cardRepository.save(card);
        updateOperator(boardId, card, operatorIds);
        card.setOrderIndex(card.getId());
        return new CardResponseDto(card);
    }

    @Transactional
    public CardResponseDto getCard(Long boardId, Long columnId, Long cardId) {
        Card card = getCardById(cardId);
        return new CardResponseDto(card);
    }
    @Transactional
    public CardResponseDto updateCard(Long boardId,Long cardId, String cardName, String cardDescription,
                                      String color, List<Long> operatorIds, LocalDate dueDate) {
        Card card = getCardById(cardId);
        card.update(cardName, cardDescription, color, dueDate);
        updateOperator(boardId, card, operatorIds);
        cardRepository.save(card);
        return new CardResponseDto(card);
    }
    @Transactional
    public void updateOperator(Long boardId, Card card, List<Long> operatorIds) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        List<Participant> participants = participantRepository.findByIdIn(operatorIds);
        List<Participant> operatorsInCard = card.getOperators().stream().map(Operator::getParticipant).toList();
        for (Participant participant : participants) {
            if(!boardId.equals(participant.getBoard().getId())){
                throw new IllegalArgumentException("보드에 해당하지 않는 참여자 입니다.");
            }
            if (!operatorsInCard.contains(participant)) {
                Operator operator = new Operator(card,participant);
                operatorRepository.save(operator);
                card.updateOperator(operator);
            }
        }
    }

    @Transactional
    public void deleteCard(Long cardId) {
        Card card = getCardById(cardId);
        cardRepository.delete(card);
    }

//    @Transactional
//    public CardResponseDto updateCardColumn(Long cardId, Long from, Long to) {
//        Col fromCol = getColById(from);
//        Card card = getCardById(cardId);
//        removeCardAssociations(fromCol,card);
//        return setCardColumn(card,to);
//    }
//
//    //TODO::Column에 대한 처리 필요 -> 같은 Col 일때만 가능하도록? Col에서 처리하지 않고 여기서 처리해야됨(column 참조를 늘리던가)
//    @Transactional
//    public void updateCardOrder(Long cardId, Long cardOrderIndex) {
//        //카드레포지토리를 뒤질게 아니라 column에서 순차적 서치를 해야됨. 아니면 다른 컬럼에서 조회할 수 있음
//        Card A = getCardById(cardId);
//        Card B = getCardById(cardOrderIndex);
//
//        Long prevAId = A.getPreviousCardId();
//        Long nextAId = A.getNextCardId();
//        Long prevBId = B.getPreviousCardId();
//        Long nextBId = B.getNextCardId();
//
//        Card prevA = prevAId == null? null : getCardById(prevAId);
//        Card nextA = nextAId == null? null : getCardById(nextAId);
//        Card prevB = prevBId == null? null : getCardById(prevBId);
//        Card nextB = nextBId == null? null : getCardById(nextBId);
//
//        if (B.getId().equals(nextAId)){
//            A.setPreviousCardId(B.getId());
//            A.setNextCardId(nextBId);
//            B.setPreviousCardId(prevAId);
//            B.setNextCardId(A.getId());
//            if (prevA != null){
//                prevA.setNextCardId(B.getId());
//            }
//            if (nextB != null){
//                nextB.setPreviousCardId(A.getId());
//            }
//        }
//        else if (B.getId().equals(prevAId)){
//            A.setPreviousCardId(prevBId);
//            A.setNextCardId(B.getId());
//            B.setPreviousCardId(A.getId());
//            B.setNextCardId(nextAId);
//            if (nextA != null){
//                nextA.setPreviousCardId(B.getId());
//            }
//            if (prevB != null){
//                prevB.setNextCardId(A.getId());
//            }
//        }
//        else{
//            A.setPreviousCardId(prevBId);
//            A.setNextCardId(nextBId);
//            B.setPreviousCardId(prevAId);
//            B.setNextCardId(nextAId);
//            if (prevA != null){
//                prevA.setNextCardId(B.getId());
//            }
//            if (nextA != null){
//                nextA.setPreviousCardId(B.getId());
//            }
//            if (prevB != null){
//                prevB.setNextCardId(A.getId());
//            }
//            if (nextB != null){
//                nextB.setPreviousCardId(A.getId());
//            }
//        }
//    }
//    @Transactional
//    public CardResponseDto setCardColumn(Card card,Long columnId){
//        Col col = getColById(columnId);
//        Long prevId = col.addCard(card);
//
//        if(!prevId.equals(card.getId())){
//            Card prevCard = getCardById(prevId);
//            prevCard.setNextCardId(card.getId());
//            card.setPreviousCardId(prevCard.getId());
//        }
//        return new CardResponseDto(card);
//    }
//
//    @Transactional
//    public void removeCardAssociations(Col col, Card card){
//        Card prevCard = card.getPreviousCardId() != null ? getCardById(card.getPreviousCardId()) : null;
//        Card nextCard = card.getNextCardId() != null ? getCardById(card.getNextCardId()) : null;
//
//        Boolean isLast = col.deleteCard(card);
//
//        if (!isLast){
//            if(prevCard != null && nextCard != null) {
//                prevCard.setNextCardId(nextCard.getId());
//                nextCard.setPreviousCardId(prevCard.getId());
//            } else if(prevCard != null) {
//                prevCard.setNextCardId(null);
//            } else if(nextCard != null) {
//                nextCard.setPreviousCardId(null);
//            }
//        }
//        card.setPreviousCardId(null);
//        card.setNextCardId(null);
//    }

    @Transactional
    public Col getColById(Long colId){
        return colRepository.findById(colId).orElseThrow(()-> new IllegalArgumentException("아이디에 해당하는 컬럼이 없습니다"));
    }

    @Transactional
    public Card getCardById(Long cardId){
        return cardRepository.findById(cardId).orElseThrow(()->new IllegalArgumentException("아이디에 해당하는 카드가 없습니다."));
    }


}
