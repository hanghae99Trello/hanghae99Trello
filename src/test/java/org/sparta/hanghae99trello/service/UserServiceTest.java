package org.sparta.hanghae99trello.service;

import org.junit.jupiter.api.Test;
import org.sparta.hanghae99trello.dto.UserRequestDto;
import org.sparta.hanghae99trello.dto.UserResponseDto;
import org.sparta.hanghae99trello.entity.User;
import org.sparta.hanghae99trello.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    @Rollback(true) // 테스트 이후 롤백하여 데이터베이스 상태를 유지합니다.
    void createUser_ShouldCreateUserSuccessfully() {
        // Given
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("John Doe");
        requestDto.setEmail("john.doe@example.com");
        requestDto.setPassword("password123");
        requestDto.setPhone("123-456-7890");

        // When
        UserResponseDto responseDto = userService.createUser(requestDto);

        // Then
        assertNotNull(responseDto);
        assertNotNull(responseDto.getId());

        // Additional assertions based on your business logic
        User createdUser = userService.getUser(responseDto.getId());
        assertEquals("John Doe", createdUser.getName());
        assertEquals("john.doe@example.com", createdUser.getEmail());
        // Add more assertions as needed
    }

    // Add more test methods as needed
}
