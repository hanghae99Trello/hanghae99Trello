package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.entity.Participant;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class BoardRequestDto {
    private String boardName;
    private String boardColor;
    private String boardDescription;
    private Set<String> participants;

    public BoardRequestDto(String todo, String hashtag, String firstBoard, Set<String> participants) {
        this.boardName = todo;
        this.boardColor = hashtag;
        this.boardDescription = firstBoard;
        this.participants = participants;
    }
}
