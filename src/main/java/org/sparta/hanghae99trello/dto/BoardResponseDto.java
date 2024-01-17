package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.entity.Board;
import org.sparta.hanghae99trello.entity.Col;
import org.sparta.hanghae99trello.entity.Participant;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class BoardResponseDto {
    private Long id;
    private String boardName;
    private String boardColor;
    private String boardDescription;
    private Set<String> participants;
    private List<Col> colList;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.boardName = board.getBoardName();
        this.boardColor = board.getBoardColor();
        this.boardDescription = board.getBoardDescription();
        this.participants = board.getParticipants().stream()
                .map(Participant::getParticipantName)
                .collect(Collectors.toSet());
        this.colList = board.getColList();
    }
}
