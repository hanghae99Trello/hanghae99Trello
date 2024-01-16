package org.sparta.hanghae99trello.service;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.dto.BoardRequestDto;
import org.sparta.hanghae99trello.dto.BoardResponseDto;
import org.sparta.hanghae99trello.entity.Board;
import org.sparta.hanghae99trello.entity.Participant;
import org.sparta.hanghae99trello.entity.User;
import org.sparta.hanghae99trello.message.ErrorMessage;
import org.sparta.hanghae99trello.repository.BoardRepository;
import org.sparta.hanghae99trello.repository.ParticipantRepository;
import org.sparta.hanghae99trello.repository.UserRepository;
import org.sparta.hanghae99trello.security.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public void createBoard(BoardRequestDto requestDto) {
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
        new BoardResponseDto(savedBoard);
    }

    @Transactional
    public List<BoardResponseDto> getBoards() {
        return boardRepository.findAll().stream().map(BoardResponseDto::new).collect(Collectors.toList());
    }

    @Transactional
    public BoardResponseDto updateBoard(Long boardId, BoardRequestDto requestDto) {
        Board board = boardRepository.findById(boardId).orElseThrow(() ->
                new IllegalArgumentException(ErrorMessage.EXIST_BOARD_ERROR_MESSAGE.getErrorMessage()));

        if (!checkCreatedByUser(board)) {
            throw new IllegalArgumentException(ErrorMessage.UPDATE_BOARD_AUTH_ERROR_MESSAGE.getErrorMessage());
        }

        board.setBoardName(requestDto.getBoardName());
        board.setBoardColor(requestDto.getBoardColor());
        board.setBoardDescription(requestDto.getBoardDescription());

        updateParticipants(board, requestDto.getParticipants());

        return new BoardResponseDto(boardRepository.save(board));
    }

    private void updateParticipants(Board board, Set<String> participantNames) {
        for (String participantName : participantNames) {
            User user = userRepository.findByName(participantName);
            Participant participant = new Participant(user, participantName);
            participant.setBoard(board);
            board.getParticipants().add(participant);
        }
    }

    private Set<Participant> convertStringArrayToParticipants(Set<String> participantNames) {
        Set<Participant> participants = new HashSet<>();
        for (String participantName : participantNames) {
            User user = userRepository.findByName(participantName);
            if (user != null) {
                participants.add(new Participant(user, user.getName()));
            }
        }
        return participants;
    }

    private boolean checkCreatedByUser(Board board) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long loggedInId = userDetails.getId();
        Long boardCreatorId = board.getCreatedBy().getId();
        return loggedInId.equals(boardCreatorId);
    }
}
