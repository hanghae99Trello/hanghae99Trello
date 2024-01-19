package org.sparta.hanghae99trello.controller;

import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.dto.BoardRequestDto;
import org.sparta.hanghae99trello.dto.BoardResponseDto;
import org.sparta.hanghae99trello.entity.User;
import org.sparta.hanghae99trello.message.SuccessMessage;
import org.sparta.hanghae99trello.repository.UserRepository;
import org.sparta.hanghae99trello.security.UserDetailsImpl;
import org.sparta.hanghae99trello.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/boards")
    public BoardResponseDto createBoard(@RequestBody BoardRequestDto requestDto) {
        return boardService.createBoard(requestDto);
    }

    @GetMapping("/boards/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoardById(@PathVariable Long boardId) {
        BoardResponseDto boardResponseDto = boardService.getBoardById(boardId);
        return ResponseEntity.ok(boardResponseDto);
    }

    @PutMapping("/boards/{boardId}")
    public ResponseEntity<BoardResponseDto> updateBoard(@PathVariable Long boardId, @RequestBody BoardRequestDto requestDto) {
        BoardResponseDto boardResponseDto = boardService.updateBoard(boardId, requestDto);
        return ResponseEntity.ok(boardResponseDto);
    }

    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.status(HttpStatus.OK).body(SuccessMessage.DELETE_SUCCESS_MESSAGE.getSuccessMessage());
    }
}
