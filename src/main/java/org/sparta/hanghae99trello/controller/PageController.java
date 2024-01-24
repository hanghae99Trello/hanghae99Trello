package org.sparta.hanghae99trello.controller;

import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.dto.BoardResponseDto;
import org.sparta.hanghae99trello.dto.CardResponseDto;
import org.sparta.hanghae99trello.dto.ColResponseDto;
import org.sparta.hanghae99trello.entity.Board;
import org.sparta.hanghae99trello.security.UserDetailsImpl;
import org.sparta.hanghae99trello.service.BoardService;
import org.sparta.hanghae99trello.service.CardService;
import org.sparta.hanghae99trello.service.ColService;
import org.sparta.hanghae99trello.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final UserService userService;
    private final BoardService boardService;
    private final ColService colService;
    private final CardService cardService;

    @GetMapping("/")
    public String getUserDetails(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId();
        String userName = userDetails.getUser().getName();
        String userEmail = userDetails.getUser().getEmail();
        String userPhone = userDetails.getUser().getPhone();
        List<Board> userBoards = userService.getUserBoards(userId);

        model.addAttribute("userId", userId);
        model.addAttribute("userName", userName);
        model.addAttribute("userEmail", userEmail);
        model.addAttribute("userPhone", userPhone);
        model.addAttribute("userBoards", userBoards);

        return "index";
    }

    @GetMapping("/api/user/login-page")
    public String signPage() {
        return "sign";
    }

    @GetMapping("/users/boards/{boardId}")
    public String boardPage(@PathVariable Long boardId, Model model) {
        BoardResponseDto board = boardService.getBoardById(boardId);
        model.addAttribute("board", board);

        return "board";
    }

    @GetMapping("/users/boards/{boardId}/columns")
    public String getColumns(@PathVariable Long boardId, Model model) {
        BoardResponseDto board = boardService.getBoardById(boardId);
        List<ColResponseDto> columns = colService.getCols(boardId);

        model.addAttribute("board", board);
        model.addAttribute("columns", columns);
        return "board";
    }

    @GetMapping("/users/boards/{boardId}/columns/{columnId}/cards/{cardId}")
    public String getColumns(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long boardId,
            @PathVariable Long columnId,
            @PathVariable Long cardId,
            Model model
    ) {
        String userName = userDetails.getUser().getName();
        model.addAttribute("userName", userName);
        CardResponseDto card = cardService.getCard(cardId);
        model.addAttribute("card", card);
        return "card";
    }
}
