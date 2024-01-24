package org.sparta.hanghae99trello.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.sparta.hanghae99trello.dto.CardColOrderRequestDto;
import org.sparta.hanghae99trello.dto.CardResponseDto;
import org.sparta.hanghae99trello.entity.*;
import org.sparta.hanghae99trello.message.ErrorMessage;
import org.sparta.hanghae99trello.repository.CardRepository;
import org.sparta.hanghae99trello.repository.ColRepository;
import org.sparta.hanghae99trello.repository.OperatorRepository;
import org.sparta.hanghae99trello.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

//TODO:: 유저 권한 확인 필요
@Service
@RequiredArgsConstructor
public class CardService {
    private final BoardService boardService;
    private final ColService colService;
    private final CardRepository cardRepository;
    private final ColRepository colRepository;
    private final RedissonClient redissonClient;
    private final ParticipantRepository participantRepository;
    private final OperatorRepository operatorRepository;


    @Transactional
    public CardResponseDto createCard(Long boardId, Long columnId, String cardName,
                                      String cardDescription, String color, List<Long> operatorIds, String dueDate) {
        RLock boardLock = boardService.createBoardLock(boardId);
        RLock colLock = colService.createColLock(columnId);

        try{
            Col col = getColById(columnId);
            Card card = new Card(cardName, cardDescription, color,col, dueDate);
            cardRepository.save(card);
            updateOperator(boardId, card, operatorIds);
            card.setOrderIndex(card.getId());
            col.addCard(card);
            return new CardResponseDto(card);
        } finally {
            colLock.unlock();
            boardLock.unlock();
        }
    }

    @Transactional
    public CardResponseDto getCard(Long boardId, Long columnId, Long cardId) {
        Card card = getCardById(cardId);
        return new CardResponseDto(card);
    }

    @Transactional
    public CardResponseDto updateCard(Long boardId, Long cardId, String cardName, String cardDescription,
                                      String color, List<Long> operatorIds, String dueDate) {
        RLock boardLock = boardService.createBoardLock(boardId);
        RLock cardLock = createCardLock(cardId);

        try {
            Card card = getCardById(cardId);
            card.update(cardName, cardDescription, color, dueDate);
            updateOperator(boardId, card, operatorIds);
            cardRepository.save(card);
            return new CardResponseDto(card);
        } finally {
            cardLock.unlock();
            boardLock.unlock();
        }

    }

    @Transactional
    public void deleteCard(Long cardId) {
        Card card = getCardById(cardId);
        Col col = card.getCol();
        Board board = col.getBoard();

        RLock boardLock = boardService.createBoardLock(board.getId());
        RLock colLock = colService.createColLock(col.getId());
        RLock cardLock = createCardLock(cardId);
        try{
            col.deleteCard(card);
            cardRepository.delete(card);
        } finally {
            cardLock.unlock();
            colLock.unlock();
            boardLock.unlock();
        }
    }

    @Transactional
    public void updateOperator(Long boardId, Card card, List<Long> operatorIds) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        List<Participant> participants = participantRepository.findByIdIn(operatorIds);
        List<Participant> operatorsInCard = card.getOperators().stream().map(Operator::getParticipant).toList();
        for (Participant participant : participants) {
            if (!boardId.equals(participant.getBoard().getId())) {
                throw new IllegalArgumentException("보드에 해당하지 않는 참여자 입니다.");
            }
            if (!operatorsInCard.contains(participant)) {
                Operator operator = new Operator(card, participant);
                operatorRepository.save(operator);
                card.updateOperator(operator);
            }
        }
    }

    @Transactional
    public CardResponseDto updateCardColOrder(Long boardId, Long columnId, Long cardId, Long newCardIndex, Long newColIndex) {

        RLock boardLock = boardService.createBoardLock(boardId);
        RLock colLock = colService.createColLock(columnId);
        RLock cardLock = createCardLock(cardId);

        try{
            Col col = getColById(columnId);
            Card card = getCardById(cardId);

            col.deleteCard(card);

            Col newCol = getColById(newColIndex);
            List<Card> cardList = cardRepository.findAllByColIdOrderByOrderIndexAsc(newCol.getId());

            double newOrderIndex = calculateNewOrderIndex(newCardIndex, cardList);
            card.setOrderIndex(newOrderIndex);

            BigDecimal bd = new BigDecimal(Double.toString(newOrderIndex));
            int precision = bd.precision();
            if (precision >= 13) {
                sortCardList(cardList);
            }

            card.updateCol(newCol);
            newCol.addCard(card);

            return new CardResponseDto(card);
        } finally {
            cardLock.unlock();
            colLock.unlock();
            boardLock.unlock();
        }
    }

    @Transactional
    public void sortCardList(List<Card> cardList){
        double count = 1;
        for (Card card : cardList) {
            card.setOrderIndex(count);
            cardRepository.save(card);
            count += 1;
        }
    }

    @Transactional
    public double calculateNewOrderIndex(Long newCardIndex, List<Card> cardList){
        if(newCardIndex > cardList.size()){
            throw new IllegalArgumentException("존재하지 않는 인덱스 입니다.");
        }

        double prevOrderIndex = 0;
        if (newCardIndex>0){
            prevOrderIndex = cardList.get(newCardIndex.intValue() - 1).getOrderIndex();
        }

        double nextOrderIndex = prevOrderIndex + 2;
        if (newCardIndex < cardList.size()){
            nextOrderIndex = cardList.get(newCardIndex.intValue()).getOrderIndex();
        }
        return (prevOrderIndex + nextOrderIndex) / 2;
    }

    @Transactional
    public Col getColById(Long colId) {
        return colRepository.findById(colId).orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 컬럼이 없습니다"));
    }

    @Transactional
    public Card getCardById(Long cardId) {
        return cardRepository.findById(cardId).orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 카드가 없습니다."));
    }

    private RLock createCardLock(Long cardId) {
        String lockKey = "CardLock" + cardId.toString();
        RLock lock = redissonClient.getLock(lockKey);

        if (!lock.tryLock()) {
            throw new RuntimeException(ErrorMessage.LOCK_NOT_ACQUIRED_ERROR_MESSAGE.getErrorMessage());
        }
        return lock;
    }
}
