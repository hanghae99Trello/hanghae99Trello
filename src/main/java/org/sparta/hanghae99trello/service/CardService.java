package org.sparta.hanghae99trello.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.sparta.hanghae99trello.dto.CardResponseDto;
import org.sparta.hanghae99trello.entity.Card;
import org.sparta.hanghae99trello.entity.Col;
import org.sparta.hanghae99trello.entity.Operator;
import org.sparta.hanghae99trello.entity.Participant;
import org.sparta.hanghae99trello.message.ErrorMessage;
import org.sparta.hanghae99trello.repository.CardRepository;
import org.sparta.hanghae99trello.repository.ColRepository;
import org.sparta.hanghae99trello.repository.OperatorRepository;
import org.sparta.hanghae99trello.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final static int MAX_POINT_LENGTH = 13;
    private final static String CARD_LOCK_HEAD = "CardLock";
    private final ColService colService;
    private final CardRepository cardRepository;
    private final ColRepository colRepository;
    private final RedissonClient redissonClient;
    private final ParticipantRepository participantRepository;
    private final OperatorRepository operatorRepository;

    @Transactional
    public CardResponseDto createCard(Long boardId, Long columnId, String cardName,
                                      String cardDescription, String color, List<Long> operatorIds, String dueDate) {
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
        }
    }

    @Transactional
    public CardResponseDto getCard(Long cardId) {
        Card card = getCardById(cardId);
        return new CardResponseDto(card);
    }

    @Transactional
    public CardResponseDto updateCard(Long boardId, Long cardId, String cardName, String cardDescription,
                                      String color, List<Long> operatorIds, String dueDate) {
        RLock cardLock = createCardLock(cardId);

        try {
            Card card = getCardById(cardId);
            card.update(cardName, cardDescription, color, dueDate);
            updateOperator(boardId, card, operatorIds);
            cardRepository.save(card);
            return new CardResponseDto(card);
        } finally {
            cardLock.unlock();
        }
    }

    @Transactional
    public void deleteCard(Long cardId) {
        Card card = getCardById(cardId);
        Col col = card.getCol();
        RLock colLock = colService.createColLock(col.getId());
        RLock cardLock = createCardLock(cardId);

        try{
            col.deleteCard(card);
            cardRepository.delete(card);
        } finally {
            cardLock.unlock();
            colLock.unlock();
        }
    }

    @Transactional
    public void updateOperator(Long boardId, Card card, List<Long> operatorIds) {
        List<Participant> participants = participantRepository.findByIdIn(operatorIds);
        List<Participant> operatorsInCard = card.getOperators().stream().map(Operator::getParticipant).toList();
        for (Participant participant : participants) {
            if (!boardId.equals(participant.getBoard().getId())) {
                throw new IllegalArgumentException(ErrorMessage.NOT_EXIST_PARTICIPANT_ERROR_MESSAGE.getErrorMessage());
            }

            if (!operatorsInCard.contains(participant)) {
                Operator operator = new Operator(card, participant);
                operatorRepository.save(operator);
                card.updateOperator(operator);
            }
        }
    }

    @Transactional
    public CardResponseDto updateCardColOrder(Long columnId, Long cardId, Long newCardIndex, Long newColIndex) {
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

            if (precision >= MAX_POINT_LENGTH) {
                sortCardList(cardList);
            }

            card.updateCol(newCol);
            newCol.addCard(card);

            return new CardResponseDto(card);
        } finally {
            cardLock.unlock();
            colLock.unlock();
        }
    }

    private void sortCardList(List<Card> cardList){
        double count = 1;
        for (Card card : cardList) {
            card.setOrderIndex(count);
            cardRepository.save(card);
            count += 1;
        }
    }

    private double calculateNewOrderIndex(Long newCardIndex, List<Card> cardList){
        if(newCardIndex > cardList.size()){
            throw new IllegalArgumentException(ErrorMessage.NOT_EXIST_CARD_INDEX_ERROR_MESSAGE.getErrorMessage());
        }

        double prevOrderIndex = 0;
        if (newCardIndex > 0){
            prevOrderIndex = cardList.get(newCardIndex.intValue() - 1).getOrderIndex();
        }

        double nextOrderIndex = prevOrderIndex + 2;
        if (newCardIndex < cardList.size()){
            nextOrderIndex = cardList.get(newCardIndex.intValue()).getOrderIndex();
        }
        return (prevOrderIndex + nextOrderIndex) / 2;
    }

    private Col getColById(Long colId) {
        return colRepository.findById(colId).orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NOT_EXIST_COL_ERROR_MESSAGE.getErrorMessage()));
    }

    private Card getCardById(Long cardId) {
        return cardRepository.findById(cardId).orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NOT_EXIST_CARD_ERROR_MESSAGE.getErrorMessage()));
    }

    private RLock createCardLock(Long cardId) {
        String lockKey = CARD_LOCK_HEAD + cardId.toString();
        RLock lock = redissonClient.getLock(lockKey);

        if (!lock.tryLock()) {
            throw new RuntimeException(ErrorMessage.LOCK_NOT_ACQUIRED_ERROR_MESSAGE.getErrorMessage());
        }

        return lock;
    }
}
