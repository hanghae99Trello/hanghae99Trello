package org.sparta.hanghae99trello.controller;

import org.sparta.hanghae99trello.dto.UserRequestDto;
import org.sparta.hanghae99trello.entity.User;
import org.sparta.hanghae99trello.message.SuccessMessage;
import org.sparta.hanghae99trello.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/user/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("api/user/signup")
    public String signUpPage() {
        return "signup";
    }

    @PostMapping("/join")
    public ResponseEntity<String> createUser(@RequestBody UserRequestDto requestDto) {
        return handleRequest(() -> {
            userService.createUser(requestDto);
            return new ResponseEntity<>(SuccessMessage.JOIN_SUCCESS_MESSAGE.getSuccessMessage(), HttpStatus.CREATED);
        });
    }

    @GetMapping("/users/{userId}")
    public User getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestBody UserRequestDto requestDto) {
        userService.updateUser(userId, requestDto);
        return new ResponseEntity<>(SuccessMessage.UPDATE_USER_SUCCESS_MESSAGE.getSuccessMessage(), HttpStatus.CREATED);
    }

    private ResponseEntity<String> handleRequest(RequestHandler handler) {
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
