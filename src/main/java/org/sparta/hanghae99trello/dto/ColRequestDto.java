package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.entity.Board;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ColRequestDto {
    private String colName;
    private Long colIndex;

    public ColRequestDto(String colName,Long colIndex) {
        this.colName = colName;
        this.colIndex = colIndex;
    }
}
