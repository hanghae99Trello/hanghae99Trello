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
    @JoinColumn(name = "card_id")
    private Card card;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @Column(nullable = false)
    private String commentMessage;

//    public Comment(User user, Card card, String commentMessage) {
//        this.card = card;
//        this.user = user;
//        this.commentMessage = commentMessage;
//    }
}
