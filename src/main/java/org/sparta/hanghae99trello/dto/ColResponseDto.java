package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.entity.Board;
import org.sparta.hanghae99trello.entity.Col;

@Getter
@RequiredArgsConstructor
public class ColResponseDto {
    private Long id;
    private String colName;
    private Long colIndex;

    public ColResponseDto(Col col) {
        this.id = col.getId();
        this.colName = col.getColName();
        this.colIndex = col.getColIndex();
    }
}



