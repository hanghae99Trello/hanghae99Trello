package org.sparta.hanghae99trello.service;

import lombok.RequiredArgsConstructor;
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
    public UserResponseDto createUser(UserRequestDto requestDto) {

        String name = requestDto.getName();
        String nickname = requestDto.getNickname();
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();
        String phone = requestDto.getPhone();

        String encodedPassword = passwordEncoder.encode(password);
        User user = userRepository.save(new User(name, nickname, email, encodedPassword, phone));
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateUser(Long userId, UserRequestDto requestDto) {
        if (isUserSelf(userId)) {
            throw new IllegalArgumentException(ErrorMessage.UPDATE_USER_AUTH_ERROR_MESSAGE.getErrorMessage());
        }

        User user = findUser(userId);

        if (requestDto.getName() != null) {
            user.setName(requestDto.getName());
        }

        if (requestDto.getPhone() != null) {
            user.setPhone(requestDto.getPhone());
        }

        return new UserResponseDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (isUserSelf(userId)) {
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
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NOT_EXIST_USER_ERROR_MESSAGE.getErrorMessage()));

        return new ArrayList<>(user.getCreatedBoards());
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException(ErrorMessage.NOT_EXIST_USER_ERROR_MESSAGE.getErrorMessage())
        );
    }

    private boolean isUserSelf(Long userId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long loggedInUserId = userDetails.getId();
        return !loggedInUserId.equals(userId);
    }
}
