package org.sparta.hanghae99trello.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.sparta.hanghae99trello.dto.UserRequestDto;
import org.sparta.hanghae99trello.dto.UserResponseDto;
import org.sparta.hanghae99trello.entity.User;
import org.sparta.hanghae99trello.repository.BoardRepository;
import org.sparta.hanghae99trello.repository.ParticipantRepository;
import org.sparta.hanghae99trello.repository.UserRepository;
import org.sparta.hanghae99trello.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private UserRepository userRepository;

    @Spy
    private PasswordEncoder passwordEncoder; // New mock for PasswordEncoder


    @Test
    void createUser_lockAcquired() {
        // given
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UserRequestDto requestDto = new UserRequestDto("John Doe", "john@example.com", "password123", "123456789", "AUTH_USER");
        String encryptedPw = encoder.encode(requestDto.getPassword());
        RLock mockLock = Mockito.mock(RLock.class);

        // Mocking behavior
        Mockito.when(mockLock.tryLock()).thenReturn(true);
        
        doReturn(new User(requestDto.getName(), requestDto.getEmail(), encryptedPw, requestDto.getPhone()))
                .when(userRepository).save(any(User.class));

        //when
        UserResponseDto responseDto = userService.createUser(requestDto);


        //then
        assertThat(responseDto.getEmail()).isEqualTo(requestDto.getEmail());

        // verify
        verify(userRepository, times(1)).save(any(User.class));
    }
}



        //        MockitoAnnotations.openMocks(this);
//        System.out.println("테스트 스레드(" + Thread.currentThread().getId() + ")가 lock을 가지고 있음");
//
//        // Mocking the necessary objects
//        UserRequestDto requestDto = new UserRequestDto("John Doe", "john@example.com", "password123", "123456789", "AUTH_USER");
//        RLock mockLock = Mockito.mock(RLock.class);
//        User savedUser = new User("John Doe", "john@example.com", "encodedPassword", "123456789");
//
//        // Mocking behavior
//        Mockito.when(mockLock.tryLock()).thenReturn(true);
//
//// createUser 메서드를 호출하여 락을 얻은 경우
//        UserResponseDto responseDto = userService.createUser(requestDto);
//
//// tryLock()가 성공한 경우에만 락이 unlock되는지 확인
//        if (responseDto != null) {
//            Mockito.verify(mockLock).unlock();
//        }
//
//// userRepository.save()가 예상된 User 객체로 한 번 호출되었는지 확인
//        Mockito.verify(userRepository, times(1)).save(Mockito.any(User.class));

//    }
//
//    @Test
//    void createUser_lockNotAcquired() {
//        MockitoAnnotations.openMocks(this);
//
//        // Mocking the necessary objects
//        UserRequestDto requestDto = new UserRequestDto("John Doe", "john@example.com", "password123", "123456789", "AUTH_USER");
//        RLock mockLock = Mockito.mock(RLock.class);
//
//        // Mocking behavior
//        Mockito.when(redissonClient.getLock("createUserLock")).thenReturn(mockLock);
//        Mockito.when(mockLock.tryLock()).thenReturn(false);
//
//        // Test the createUser method when the lock is not acquired
//        assertThrows(RuntimeException.class, () -> userService.createUser(requestDto));
//
//        // Verify that the lock is not unlocked (tryLock was not successful, so no need to unlock)
//        verify(mockLock, Mockito.never()).unlock();
//
//        // Verify that userRepository.save() is never called in this case
//        verify(userRepository, Mockito.never()).save(any(User.class));
//    }
//}
