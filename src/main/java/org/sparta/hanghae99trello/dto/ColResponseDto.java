package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.sparta.hanghae99trello.entity.Board;
import org.sparta.hanghae99trello.entity.Card;
import org.sparta.hanghae99trello.entity.Col;

import java.util.List;

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



