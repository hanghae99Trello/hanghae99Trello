package org.sparta.hanghae99trello.controller;

import org.sparta.hanghae99trello.dto.UserRequestDto;
import org.sparta.hanghae99trello.message.SuccessMessage;
import org.sparta.hanghae99trello.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class SignController {
    private UserService userService;

    @GetMapping("/api/user/login-page")
    public String signPage() {
        return "sign";
    }

    @PostMapping("/api/join")
    public ResponseEntity<String> createUser(@RequestBody UserRequestDto requestDto) {
        return handleRequest(() -> {
            userService.createUser(requestDto);
            return new ResponseEntity<>(SuccessMessage.JOIN_SUCCESS_MESSAGE.getSuccessMessage(), HttpStatus.CREATED);
        });
    }

    private ResponseEntity<String> handleRequest(SignController.RequestHandler handler) {
        try {
            return handler.handle();
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @FunctionalInterface
    private interface RequestHandler {
        ResponseEntity<String> handle();
    }
}
