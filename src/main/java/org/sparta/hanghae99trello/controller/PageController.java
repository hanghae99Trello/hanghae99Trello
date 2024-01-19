package org.sparta.hanghae99trello.controller;

import org.sparta.hanghae99trello.entity.Board;
import org.sparta.hanghae99trello.security.UserDetailsImpl;
import org.sparta.hanghae99trello.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PageController {
    private final UserService userService;

    public PageController(UserService userService) {
        this.userService = userService;
    }

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
}
