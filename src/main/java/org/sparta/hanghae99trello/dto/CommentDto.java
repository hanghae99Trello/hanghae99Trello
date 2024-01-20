package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sparta.hanghae99trello.entity.Comment;

@Getter
@Setter
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private Long participantId;
    private String commentMessage;

    public CommentDto(Comment comment){
        this.id = comment.getId();
        this.participantId = comment.getParticipant().getId();
        this.commentMessage = comment.getCommentMessage();
    }
}
