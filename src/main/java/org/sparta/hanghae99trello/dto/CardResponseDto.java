package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sparta.hanghae99trello.entity.Card;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CardResponseDto {

    private String cardName;
    private String cardDescription;
    private String color;
    private List<OperatorDto> operators;
    private List<CommentDto> commentList;
    private String dueDate;

    public CardResponseDto(Card card) {
        this.cardName = card.getCardName();
        this.cardDescription = card.getCardDescription();
        this.color = card.getCardColor();
        this.operators = card.getOperators().stream().map(OperatorDto::new).collect((Collectors.toList()));
        this.commentList = card.getCommentList().stream().map(CommentDto::new).collect((Collectors.toList()));
        this.dueDate = card.getDueDate();
    }
}
