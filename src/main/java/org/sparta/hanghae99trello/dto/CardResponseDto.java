package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sparta.hanghae99trello.entity.Card;
import org.sparta.hanghae99trello.entity.Comment;
import org.sparta.hanghae99trello.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CardResponseDto {

    private String cardName;
    private String cardDescription;
    private String color;
    private List<Long> operator;
    private List<String> commentList;
    private LocalDate dueDate;

    public CardResponseDto(Card card) {
        this.cardName = card.getCardName();
        this.cardDescription = card.getCardDescription();
        this.color = card.getCardColor();
        this.operator = card.getOperators().stream().map(User::getId).collect((Collectors.toList()));
        this.commentList = card.getCommentList().stream().map(Comment::getCommentMessage).collect((Collectors.toList()));
        this.dueDate = card.getDueDate();
    }
}
