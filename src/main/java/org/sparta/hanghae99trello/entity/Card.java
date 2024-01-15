package org.sparta.hanghae99trello.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @jakarta.persistence.Column(nullable = false)
    private String cardName;

    @jakarta.persistence.Column(nullable = false)
    private String cardDescription;

    @jakarta.persistence.Column(nullable = false)
    private String cardColor;

//    @OneToOne
//    private Card prevCard;
//
//    @OneToOne
//    private Card nextCard;

    //operator 정보
    @ManyToMany
    @JoinTable(
            name = "operator",
            joinColumns =  @JoinColumn(name = "card_ids"),
            inverseJoinColumns = @JoinColumn(name = "user_ids")
    )
    private List<User> operators;

    //댓글 정보
    @OneToMany(mappedBy = "card",cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<Comment> commentList;

    @Column(nullable = true)
    private LocalDate dueDate;

    public Card(String cardName, String cardDescription, String color, List<User> operators) {
        this.cardName = cardName;
        this.cardDescription = cardDescription;
        this.cardColor = color;
        this.operators = operators != null ? operators : new ArrayList<>();
        this.commentList = new ArrayList<>();
    }

    public void addComment(Comment comment) {
        this.commentList.add(comment);
    }

    public void update(String cardName, String cardDescription, String color, List<User> operator, LocalDate dueDate) {
        this.cardName = cardName;
        this.cardDescription = cardDescription;
        this.cardColor = color;
        this.operators = operator;
        this.dueDate = dueDate;
    }
}
