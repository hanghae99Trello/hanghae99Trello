package org.sparta.hanghae99trello.service;

import lombok.RequiredArgsConstructor;

import org.sparta.hanghae99trello.dto.UserRequestDto;
import org.sparta.hanghae99trello.dto.UserResponseDto;
import org.sparta.hanghae99trello.entity.User;
import org.sparta.hanghae99trello.message.ErrorMessage;
import org.sparta.hanghae99trello.repository.BoardRepository;
import org.sparta.hanghae99trello.repository.ParticipantRepository;
import org.sparta.hanghae99trello.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ParticipantRepository participantRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedissonClient redissonClient;

    @Transactional
    public void createUser(UserRequestDto requestDto) {
        String lockKey = "createUserLock"; // 락의 키 설정

        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.lock(); // 락 획득

            // createUser 메서드의 나머지 로직 수행
            String name = requestDto.getName();
            String email = requestDto.getEmail();
            String password = requestDto.getPassword();
            String phone = requestDto.getPhone();

            String encodedPassword = passwordEncoder.encode(password);
            User user = userRepository.save(new User(name, email, encodedPassword, phone));
            new UserResponseDto(user);

        } finally {
            lock.unlock(); // 락 해제

        }
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(ErrorMessage.EXIST_USER_ERROR_MESSAGE.getErrorMessage()));
    }

    public void updateUser(Long id, UserRequestDto requestDto) {
        User user = findUser(id);
        user.update(requestDto);

    }

//    public void deleteUser(Long userId) {
//        User user = findUser(userId);
//
//        List<Board> boards = boardRepository.findByCreatedBy(user);
//
//        for (Board board : boards) {
//            participantRepository.deleteByBoardId(board.getId());
//            boardRepository.deleteById(board.getId());
//        }
//
//        userRepository.delete(user);
//    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException(ErrorMessage.EXIST_USER_ERROR_MESSAGE.getErrorMessage())
        );
    }
}
