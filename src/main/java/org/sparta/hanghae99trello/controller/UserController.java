package org.sparta.hanghae99trello.controller;

import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.dto.UserRequestDto;
import org.sparta.hanghae99trello.dto.UserResponseDto;
import org.sparta.hanghae99trello.entity.User;
import org.sparta.hanghae99trello.message.SuccessMessage;
import org.sparta.hanghae99trello.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<String> createUser(@RequestBody UserRequestDto requestDto) {
        return handleRequest(() -> {
            UserResponseDto responseDto = userService.createUser(requestDto);
            return new ResponseEntity<>(SuccessMessage.JOIN_SUCCESS_MESSAGE.getSuccessMessage(), HttpStatus.CREATED);
        });
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long userId, @RequestBody UserRequestDto requestDto) {
        UserResponseDto userResponseDto = userService.updateUser(userId, requestDto);
        return ResponseEntity.ok(userResponseDto);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(SuccessMessage.DELETE_SUCCESS_MESSAGE.getSuccessMessage(), HttpStatus.CREATED);
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
