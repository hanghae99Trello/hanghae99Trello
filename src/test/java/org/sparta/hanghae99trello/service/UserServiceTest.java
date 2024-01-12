package org.sparta.hanghae99trello.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sparta.hanghae99trello.dto.UserRequestDto;
import org.sparta.hanghae99trello.dto.UserResponseDto;
import org.sparta.hanghae99trello.entity.User;
import org.sparta.hanghae99trello.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    private static Stream<Arguments> provideUserTestData() {
        return Stream.of(
                Arguments.of(new UserRequestDto("mi", "mi@email.com", "1234", "USER"))
        );
    }

    @ParameterizedTest
    @MethodSource("provideUserTestData")
    @Transactional
    @Rollback(value = true)
    @DisplayName("회원가입 테스트")
    void createUser(UserRequestDto requestDto) {
        UserResponseDto responseDto = userService.createUser(requestDto);

        assertNotNull(responseDto);
        assertEquals(requestDto.getName(), responseDto.getName());
        assertEquals(requestDto.getEmail(), responseDto.getEmail());

        User savedUser = userRepository.findByName(requestDto.getName());
        assertNotNull(savedUser);
        assertEquals(requestDto.getEmail(), savedUser.getEmail());
    }
}
