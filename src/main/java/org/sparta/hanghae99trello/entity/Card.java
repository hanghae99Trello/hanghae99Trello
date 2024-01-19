package org.sparta.hanghae99trello.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cardName;

    @Column(nullable = false)
    private String cardDescription;

    @Column(nullable = false)
    private String cardColor;

    @OneToMany(mappedBy = "card",cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

    @Column(nullable = true)
    private LocalDate dueDate;

    @OneToMany
    private List<Operator> operators;

    @Column
    @Setter
    private double index;

    public Card(String cardName, String cardDescription, String color) {
        this.cardName = cardName;
        this.cardDescription = cardDescription;
        this.cardColor = color;
        this.operators = new ArrayList<>();
        this.commentList = new ArrayList<>();
    }

    public void addComment(Comment comment) {
        this.commentList.add(comment);
    }

    public void updateOperator(Operator operator) {
        this.operators.add(operator);
    }

//    @Setter
//    @JoinColumn(name = "previous_card_id")
//    private Long previousCardId;

//    @Setter
//    @JoinColumn(name = "next_card_id")
//    private Long nextCardId;
//
//    public Card(String cardName, String cardDescription, String color, List<User> operators) {
//        this.cardName = cardName;
//        this.cardDescription = cardDescription;
//        this.cardColor = color;
//        this.operators = operators != null ? operators : new ArrayList<>();
//        this.commentList = new ArrayList<>();
//    }
//

//    public void update(String cardName, String cardDescription, String color, List<User> operator, LocalDate dueDate) {
//        this.cardName = cardName;
//        this.cardDescription = cardDescription;
//        this.cardColor = color;
//        this.operators = operator;
//        this.dueDate = dueDate;
//    }


}
