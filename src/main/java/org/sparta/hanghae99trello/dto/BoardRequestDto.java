package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

    public BoardRequestDto(String boardName, String boardColor, String boardDescription, Set<String> participants) {
        this.boardName = boardName;
        this.boardColor = boardColor;
        this.boardDescription = boardDescription;
        this.participants = Collections.unmodifiableSet(participants);
    }
}
