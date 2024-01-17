package org.sparta.hanghae99trello.service;

import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.dto.UserRequestDto;
import org.sparta.hanghae99trello.dto.UserResponseDto;
import org.sparta.hanghae99trello.entity.User;
import org.sparta.hanghae99trello.message.ErrorMessage;
import org.sparta.hanghae99trello.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(UserRequestDto requestDto) {
        String name = requestDto.getName();
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();
        String phone = requestDto.getPhone();

        String encodedPassword = passwordEncoder.encode(password);
        User user = userRepository.save(new User(name, email, encodedPassword, phone));
        new UserResponseDto(user);
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
