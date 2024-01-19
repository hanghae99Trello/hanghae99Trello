package org.sparta.hanghae99trello.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    @Column(nullable = false)
    private String commentMessage;

    public Comment(Participant participant, Card card, String commentMessage) {
        this.participant = participant;
        this.card = card;
        this.commentMessage = commentMessage;
    }
}
