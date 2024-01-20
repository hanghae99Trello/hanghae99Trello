package org.sparta.hanghae99trello.entity;

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
@Table(name = "cards")
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

    @OneToMany(mappedBy = "card", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

    @OneToMany(mappedBy = "card", cascade = CascadeType.REMOVE)
    private List<Operator> operators;

    @Column(nullable = true)
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "col_id")
    private Col col;

    @Column
    @Setter
    private double orderIndex;

    public Card(String cardName, String cardDescription, String color, Col col) {
        this.cardName = cardName;
        this.cardDescription = cardDescription;
        this.cardColor = color;
        this.col = col;
        this.operators = new ArrayList<>();
        this.commentList = new ArrayList<>();
    }

    public void addComment(Comment comment) {
        this.commentList.add(comment);
    }

    public void updateOperator(Operator operator) {
        this.operators.add(operator);
    }

    public void update(String cardName, String cardDescription, String color, LocalDate dueDate) {
        this.cardName = cardName;
        this.cardDescription = cardDescription;
        this.cardColor = color;
        this.dueDate = dueDate;
    }

    public void updateCol(Col newCol) {
        this.col = newCol;
    }
}
