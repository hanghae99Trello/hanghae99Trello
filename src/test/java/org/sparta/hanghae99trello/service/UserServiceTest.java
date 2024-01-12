package org.sparta.hanghae99trello.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sparta.hanghae99trello.dto.UserRequestDto;
import org.sparta.hanghae99trello.dto.UserResponseDto;
import org.sparta.hanghae99trello.entity.User;
import org.sparta.hanghae99trello.repository.UserRepository;
import org.sparta.hanghae99trello.security.UserAuthEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    private static Stream<Arguments> provideUserTestData() {
        return Stream.of(
                Arguments.of(new UserRequestDto("kim", "kim@email.com", "1234", "010-1234-1234", UserAuthEnum.USER.getAuthority()))
        );
    }

    private static Stream<Arguments> provideUserUpdateTestData() {
        return Stream.of(
                Arguments.of(2L, new UserRequestDto("da", "da@email.com", null, "010-1234-5678", UserAuthEnum.USER.getAuthority()))
        );
    }

    @ParameterizedTest
    @MethodSource("provideUserTestData")
    @Transactional
    @Rollback(value = false)
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

    @ParameterizedTest
    @MethodSource("provideUserUpdateTestData")
    @Transactional
    @Rollback(value = false)
    @DisplayName("회원정보 수정테스트")
    void updateUser(Long userId, UserRequestDto requestDto) {
        User user = userService.updateUser(userId, requestDto);

        assertThat(requestDto.getName()).isEqualTo("da");
        assertThat(requestDto.getEmail()).isEqualTo("da@email.com");
    }
}
