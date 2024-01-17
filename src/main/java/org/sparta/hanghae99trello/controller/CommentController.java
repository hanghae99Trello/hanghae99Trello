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

    //TODO :: 엔드포인트에 필요없는 값들 수정필요
    @PostMapping("/{boardId}/columns/{columnId}/cards/{cardId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long cardId,
                                                            @RequestBody CommentRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails){

        CommentResponseDto commentResponseDto = commentService.createComment(userDetails.getUser(),cardId,requestDto.getCommentMessage());
        return ResponseEntity.status(HttpStatus.OK).body(commentResponseDto);
    }
}
