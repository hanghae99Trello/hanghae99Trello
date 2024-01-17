package org.sparta.hanghae99trello.controller;

import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.dto.ColRequestDto;
import org.sparta.hanghae99trello.dto.ColResponseDto;
import org.sparta.hanghae99trello.message.SuccessMessage;
import org.sparta.hanghae99trello.repository.BoardRepository;
import org.sparta.hanghae99trello.service.BoardService;
import org.sparta.hanghae99trello.service.ColService;
import org.sparta.hanghae99trello.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ColController {

    private final ColService colService;

    @GetMapping("/users/boards/{boardId}/columns")
    public List<ColResponseDto> getCols(@PathVariable Long boardId) {
        return colService.getCols(boardId);
    }

    @PostMapping("/users/boards/{boardId}/columns")
    public ResponseEntity<String> CreateCol(@PathVariable Long boardId, @RequestBody ColRequestDto requestDto) {
        colService.createCol(boardId, requestDto);
        return new ResponseEntity<>(SuccessMessage.CREATE_COL_SUCCESS_MESSAGE.getSuccessMessage(), HttpStatus.CREATED);
    }

    @PutMapping("/users/boards/{boardId}/columns/{columnId}")
    public ResponseEntity<ColResponseDto> updateCol(@PathVariable Long boardId, @PathVariable Long columnId, @RequestBody ColRequestDto requestDto) {
        ColResponseDto colResponseDto = colService.updateCol(boardId, columnId, requestDto);
        return ResponseEntity.ok(colResponseDto);
    }

    @DeleteMapping("/users/boards/{boardId}/columns/{columnId}")
    public ResponseEntity<String> deleteCol(@PathVariable Long boardId, @PathVariable Long columnId) {
        colService.deleteCol(boardId, columnId);
        return new ResponseEntity<>(SuccessMessage.DELETE_SUCCESS_MESSAGE.getSuccessMessage(), HttpStatus.OK);
    }

    @PutMapping("/users/boards/{boardId}/columns/{columnId}/{columnOrderIndex}")
    public ResponseEntity<ColResponseDto> updateColIdx(@PathVariable Long boardId, @PathVariable Long columnId, @PathVariable Long columnOrderIndex) {
        ColResponseDto colResponseDto = colService.updateColIdx(boardId, columnId, columnOrderIndex);
        return ResponseEntity.ok(colResponseDto);
    }
}
