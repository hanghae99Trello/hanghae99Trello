package org.sparta.hanghae99trello.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.sparta.hanghae99trello.dto.UserRequestDto;
import org.sparta.hanghae99trello.entity.User;
import org.sparta.hanghae99trello.repository.UserRepository;
import org.sparta.hanghae99trello.security.UserAuthEnum;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock lock;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void createUserTest() {
        // Redisson Mock을 사용하여 RLock 객체를 생성
        when(redissonClient.getLock(any())).thenReturn(lock);

        // lock.lock() 메서드에 대한 호출 시에 10초 동안 시간 제한을 설정하여 호출되도록 함
        doNothing().when(lock).lock(10L, SECONDS);

        // 사용자 생성 요청 DTO
        UserRequestDto requestDto = new UserRequestDto("John Doe", "john@example.com", "password123", "123456789", UserAuthEnum.USER.getAuthority());

        // 사용자 엔터티 Mock
        User mockUser = new User("John Doe", "john@example.com", "encodedPassword", "123456789");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // 테스트 대상 메서드 호출
        userService.createUser(requestDto);

        // Mockito를 사용하여 lock이 정상적으로 호출되었는지 확인
        verify(lock).lock(10L, SECONDS);
        verify(lock).unlock();

        // Mockito를 사용하여 userRepository.save 메서드가 정상적으로 호출되었는지 확인
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUserWithDistributedLock() throws InterruptedException {
        // Redisson Mock을 사용하여 RLock 객체를 생성
        when(redissonClient.getLock(any())).thenReturn(lock);

        // 분산 락을 테스트할 때 필요한 스레드 수
        int threadCount = 5;
        // CountDownLatch를 사용하여 모든 스레드가 동시에 시작되도록 함
        CountDownLatch startSignal = new CountDownLatch(1);
        // CountDownLatch를 사용하여 모든 스레드가 종료될 때까지 기다림
        CountDownLatch doneSignal = new CountDownLatch(threadCount);

        // 특정 스레드에서만 lock.tryLock(10L, SECONDS) 호출
        Runnable task = () -> {
            try {
                // 첫 번째 스레드만 락을 얻도록 함
                if (startSignal.getCount() == 1) {
                    // 모든 스레드가 동시에 시작될 때까지 대기
                    startSignal.await();

                    // 락 획득 (타임아웃 10초 설정) - tryLock이 실패하도록 설정
                    doNothing().when(lock).lock(10L, TimeUnit.SECONDS);

                    // 사용자 생성 요청 DTO
                    UserRequestDto requestDto = new UserRequestDto("John Doe", "john@example.com", "password123", "123456789", UserAuthEnum.USER.getAuthority());

                    // 사용자 엔터티 Mock
                    User mockUser = new User("John Doe", "john@example.com", "encodedPassword", "123456789");
                    when(userRepository.save(any(User.class))).thenReturn(mockUser);

                    // 테스트 대상 메서드 호출
                    userService.createUser(requestDto);

                    // Mockito를 사용하여 lock이 정상적으로 호출되었는지 확인
                    verify(lock, atLeastOnce()).lock(10L, TimeUnit.SECONDS);

                    // Mockito를 사용하여 userRepository.save 메서드가 정상적으로 호출되었는지 확인
                    verify(userRepository, atLeastOnce()).save(any(User.class));

                    // Mockito를 사용하여 lock이 정상적으로 해제되었는지 확인
                    verify(lock, atLeastOnce()).unlock();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // 스레드 종료 신호
                doneSignal.countDown();
            }
        };

        // 여러 스레드를 생성하고 시작
        for (int i = 0; i < threadCount; i++) {
            new Thread(task).start();
        }

        // 모든 스레드가 동시에 시작하도록 신호 발송
        startSignal.countDown();

        // 모든 스레드가 종료될 때까지 대기
        doneSignal.await();
    }
}