package org.sparta.hanghae99trello.service;

import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.dto.UserRequestDto;
import org.sparta.hanghae99trello.dto.UserResponseDto;
import org.sparta.hanghae99trello.entity.User;
import org.sparta.hanghae99trello.message.ErrorMessage;
import org.sparta.hanghae99trello.message.SuccessMessage;
import org.sparta.hanghae99trello.repository.UserRepository;
import org.sparta.hanghae99trello.security.UserAuthEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto createUser(UserRequestDto requestDto) {
        String name = requestDto.getName();
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();
        String phone = requestDto.getPhone();

        String encodedPassword = passwordEncoder.encode(password);
        User user = userRepository.save(new User(name, email, encodedPassword, phone));
        return new UserResponseDto(user);
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(ErrorMessage.EXIST_USER_ERROR_MESSAGE.getErrorMessage()));
    }

    public User updateUser(Long id, UserRequestDto requestDto) {
        User user = findUser(id);
        user.update(requestDto);

        return user;
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException(ErrorMessage.EXIST_USER_ERROR_MESSAGE.getErrorMessage())
        );
    }
}
