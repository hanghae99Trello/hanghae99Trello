package org.sparta.hanghae99trello.controller;


import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.dto.CardColumnRequestDto;
import org.sparta.hanghae99trello.dto.CardOrderRequestDto;
import org.sparta.hanghae99trello.dto.CardRequestDto;
import org.sparta.hanghae99trello.dto.CardResponseDto;
import org.sparta.hanghae99trello.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/boards")
public class CardController {

    private final CardService cardService;

    @PostMapping("/{boardId}/columns/{columnId}/cards")
    public ResponseEntity<CardResponseDto> createCard(@PathVariable Long boardId,
                                                      @PathVariable Long columnId,
                                                      @RequestBody CardRequestDto requestDto) {

        CardResponseDto cardResponseDto = cardService.createCard(boardId, columnId, requestDto.getCardName(),
                requestDto.getCardDescription(), requestDto.getColor(), requestDto.getOperatorIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(cardResponseDto);
    }

    @GetMapping("/{boardId}/columns/{columnId}/cards/{cardId}")
    public ResponseEntity<CardResponseDto> getCard(@PathVariable Long boardId,
                                                   @PathVariable Long columnId,
                                                   @PathVariable Long cardId) {
        CardResponseDto cardResponseDto = cardService.getCard(boardId, columnId, cardId);
        return ResponseEntity.status(HttpStatus.OK).body(cardResponseDto);
    }

    @PutMapping("/{boardId}/columns/{columnId}/cards/{cardId}")
    public ResponseEntity<CardResponseDto> updateCard(@PathVariable Long boardId,
                                                      @PathVariable Long cardId,
                                                      @RequestBody CardRequestDto requestDto) {
        CardResponseDto cardResponseDto = cardService.updateCard(boardId, cardId, requestDto.getCardName(),
                requestDto.getCardDescription(), requestDto.getColor(), requestDto.getOperatorIds(), requestDto.getDueDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(cardResponseDto);
    }

    @DeleteMapping("/{boardId}/columns/{columnId}/cards/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return new ResponseEntity<>("카드 삭제가 완료되었습니다.", HttpStatus.OK);
    }
//
//    //컬럼 간 카드 이동
//    @PutMapping("/users/boards/{boardId}/columns/{columnId}/cards/{cardId}/col")
//    public ResponseEntity<CardResponseDto> updateCardColumn(@PathVariable Long cardId,
//                                                            @PathVariable Long columnId,
//                                                            @RequestBody CardColumnRequestDto requestDto) {
//        CardResponseDto cardResponseDto = cardService.updateCardColumn(cardId, columnId, requestDto.getColumnId());
//        return ResponseEntity.status(HttpStatus.OK).body(cardResponseDto);
//    }
//
//    @PutMapping("/users/boards/{boardId}/columns/{columnId}/cards/{cardId}/order")
//    public ResponseEntity<String> updateCardOrder(@PathVariable Long cardId,
//                                                  @PathVariable Long columnId,
//                                                  @RequestBody CardOrderRequestDto requestDto) {
//        cardService.updateCardOrder(cardId, requestDto.getCardOrderIndex());
//        return new ResponseEntity<>("카드 순서가 변경되었습니다.", HttpStatus.OK);
//    }
}