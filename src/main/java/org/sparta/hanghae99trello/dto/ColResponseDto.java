package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.entity.Col;

@Getter
@RequiredArgsConstructor
public class ColResponseDto {

    private String colName;
    private Long colIndex;

    public ColResponseDto(Col col) {
        this.colName = col.getColName();
        this.colIndex = col.getColIndex();
    }
}



