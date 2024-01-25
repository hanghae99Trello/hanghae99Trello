package org.sparta.hanghae99trello.controller;


import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.dto.CommentRequestDto;
import org.sparta.hanghae99trello.dto.CommentResponseDto;
import org.sparta.hanghae99trello.security.UserDetailsImpl;
import org.sparta.hanghae99trello.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users/boards")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{boardId}/columns/{columnId}/cards/{cardId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long boardId,
                                                            @PathVariable Long cardId,
                                                            @PathVariable String columnId,
                                                            @RequestBody CommentRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails){

        CommentResponseDto commentResponseDto = commentService.createComment(userDetails.getUser().getId(),boardId, cardId,requestDto.getCommentMessage());
        return ResponseEntity.ok(commentResponseDto);
    }
}
