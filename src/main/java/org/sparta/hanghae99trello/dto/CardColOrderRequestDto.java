package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CardColOrderRequestDto {
    private Long cardIndex;
    private Long newColIndex;
}
