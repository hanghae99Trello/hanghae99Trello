package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ColRequestDto {
    private String colName;
    private Long colIndex;
    private Long boardId;

    public ColRequestDto(String colName,Long colIndex, Long boardId) {
        this.colName = colName;
        this.colIndex = colIndex;
        this.boardId = boardId;
    }
}
