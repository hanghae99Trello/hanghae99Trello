package org.sparta.hanghae99trello.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonBackReference
    private Card card;

    @ManyToOne
    @JsonIgnore
    private Participant participant;

    public Operator(Card card, Participant participant) {
        this.card = card;
        this.participant = participant;
    }
}
