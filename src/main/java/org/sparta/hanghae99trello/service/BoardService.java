package org.sparta.hanghae99trello.service;

import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.dto.BoardRequestDto;
import org.sparta.hanghae99trello.dto.BoardResponseDto;
import org.sparta.hanghae99trello.entity.Board;
import org.sparta.hanghae99trello.entity.Participant;
import org.sparta.hanghae99trello.entity.User;
import org.sparta.hanghae99trello.repository.BoardRepository;
import org.sparta.hanghae99trello.repository.UserRepository;
import org.sparta.hanghae99trello.security.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto requestDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User createdBy = userDetails.getUser();

        Set<Participant> participants = convertStringArrayToParticipants(requestDto.getParticipants());

        Board board = new Board(
                requestDto.getBoardName(),
                requestDto.getBoardColor(),
                requestDto.getBoardDescription(),
                createdBy,
                participants
        );

        for (Participant participant : participants) {
            participant.setBoard(board);
        }

        board.setParticipants(participants);
        Board savedBoard = boardRepository.save(board);
        return new BoardResponseDto(savedBoard);
    }

    public Set<Participant> convertStringArrayToParticipants(Set<String> participantNames) {
        Set<Participant> participants = new HashSet<>();
        for (String participantName : participantNames) {
            // 여기서 사용자 이름으로 사용자를 찾아오는 로직을 구현합니다.
            User user = userRepository.findByName(participantName);

            // 사용자를 찾았을 경우에만 Participant 객체를 생성합니다.
            if (user != null) {
                participants.add(new Participant(user));
            }
        }
        return participants;
    }



}
