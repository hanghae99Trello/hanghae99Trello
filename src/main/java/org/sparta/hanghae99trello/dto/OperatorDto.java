package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sparta.hanghae99trello.entity.Operator;

@Getter
@Setter
@NoArgsConstructor
public class OperatorDto {
    private Long id;
    private Long participantId;
    private Long cardId;

    public OperatorDto(Operator operator){
        this.id = operator.getId();
        this.participantId = operator.getParticipant().getId();
        this.cardId = operator.getCard().getId();
    }
}
