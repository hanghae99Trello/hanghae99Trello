package org.sparta.hanghae99trello.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.sparta.hanghae99trello.dto.UserRequestDto;
import org.sparta.hanghae99trello.dto.UserResponseDto;
import org.sparta.hanghae99trello.entity.Board;
import org.sparta.hanghae99trello.entity.User;
import org.sparta.hanghae99trello.message.ErrorMessage;
import org.sparta.hanghae99trello.repository.BoardRepository;
import org.sparta.hanghae99trello.repository.ParticipantRepository;
import org.sparta.hanghae99trello.repository.UserRepository;
import org.sparta.hanghae99trello.security.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        String lockKey = "createUserLock";

        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.lock();

            String name = requestDto.getName();
            String email = requestDto.getEmail();
            String password = requestDto.getPassword();
            String phone = requestDto.getPhone();

            String encodedPassword = passwordEncoder.encode(password);
            User user = userRepository.save(new User(name, email, encodedPassword, phone));
            new UserResponseDto(user);

        } finally {
            lock.unlock();
        }
    }

    public void updateUser(Long userId, UserRequestDto requestDto) {
        if (!checkUserSelf(userId)) {
            throw new IllegalArgumentException(ErrorMessage.UPDATE_USER_AUTH_ERROR_MESSAGE.getErrorMessage());
        }
        User user = findUser(userId);
        user.update(requestDto);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!checkUserSelf(userId)) {
            throw new IllegalArgumentException(ErrorMessage.DELETE_USER_AUTH_ERROR_MESSAGE.getErrorMessage());
        }
        User user = findUser(userId);
        List<Board> boards = boardRepository.findByCreatedBy(user);

        for (Board board : boards) {
            participantRepository.deleteByBoardId(board.getId());
            boardRepository.deleteById(board.getId());
        }

        userRepository.delete(user);
    }

    public List<Board> getUserBoards(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.EXIST_USER_ERROR_MESSAGE.getErrorMessage()));

        return new ArrayList<>(user.getCreatedBoards());
    }

    @PreDestroy
    public void preDestroy() {
        redissonClient.shutdown();
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException(ErrorMessage.EXIST_USER_ERROR_MESSAGE.getErrorMessage())
        );
    }

    private boolean checkUserSelf(Long userId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long loggedInUserId = userDetails.getId();
        return loggedInUserId.equals(userId);
    }
}
