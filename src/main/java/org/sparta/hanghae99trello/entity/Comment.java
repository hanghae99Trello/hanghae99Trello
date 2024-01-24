package org.sparta.hanghae99trello.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private Participant participant;

    @ManyToOne
    @JsonBackReference
    private Card card;

    @Column(nullable = false)
    private String commentMessage;

    public Comment(Participant participant, Card card, String commentMessage) {
        this.participant = participant;
        this.card = card;
        this.commentMessage = commentMessage;
    }
}
