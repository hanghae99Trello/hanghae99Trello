package org.sparta.hanghae99trello.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Operator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;


    public Operator(Card card, Participant participant) {
        this.card = card;
        this.participant = participant;
    }
}
