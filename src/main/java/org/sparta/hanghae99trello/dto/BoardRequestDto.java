package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.entity.Col;
import org.sparta.hanghae99trello.entity.Participant;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class BoardRequestDto {
    private String boardName;
    private String boardColor;
    private String boardDescription;
    private Set<String> participants;
    private List<ColRequestDto> colList;

    public BoardRequestDto(String boardName, String boardColor, String boardDescription, Set<String> participants, List<ColRequestDto> colList) {
        this.boardName = boardName;
        this.boardColor = boardColor;
        this.boardDescription = boardDescription;
        this.participants = Collections.unmodifiableSet(participants);
        this.colList = colList;
    }
}
