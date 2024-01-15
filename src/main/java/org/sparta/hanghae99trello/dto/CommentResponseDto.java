package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.Setter;
import org.sparta.hanghae99trello.entity.Comment;

@Getter
@Setter
public class CommentResponseDto {

    private int commentId;
    private String commentMessage;

    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getId();
        this.commentMessage = comment.getCommentMessage();
    }
}
