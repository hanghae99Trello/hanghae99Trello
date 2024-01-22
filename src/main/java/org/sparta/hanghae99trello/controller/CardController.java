package org.sparta.hanghae99trello.controller;


import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.dto.CardColOrderRequestDto;
import org.sparta.hanghae99trello.dto.CardRequestDto;
import org.sparta.hanghae99trello.dto.CardResponseDto;
import org.sparta.hanghae99trello.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
                requestDto.getCardDescription(), requestDto.getColor(), requestDto.getOperatorIds(), requestDto.getDueDate());
        return ResponseEntity.ok(cardResponseDto);
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

    @PutMapping("/{boardId}/columns/{columnId}/cards/{cardId}/col")
    public ResponseEntity<CardResponseDto> updateCardColOrder(@PathVariable Long boardId,
                                                              @PathVariable Long columnId,
                                                              @PathVariable Long cardId,
                                                              @RequestBody CardColOrderRequestDto requestDto){
        CardResponseDto cardResponseDto = cardService.updateCardColOrder(boardId, columnId, cardId, requestDto.getCardIndex(),requestDto.getNewColIndex());
        return ResponseEntity.status(HttpStatus.CREATED).body(cardResponseDto);
    }

}