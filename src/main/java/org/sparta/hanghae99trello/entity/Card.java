package org.sparta.hanghae99trello.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JsonManagedReference
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "card", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<Operator> operators = new ArrayList<>();

    @Column(nullable = true)
    private String dueDate;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonBackReference
    @JoinColumn(name = "col_id")
    private Col col;

    @Column
    @Setter
    private double orderIndex;

    public Card(String cardName, String cardDescription, String color, Col col, String dueDate) {
        this.cardName = cardName;
        this.cardDescription = cardDescription;
        this.cardColor = color;
        this.col = col;
        this.dueDate = dueDate;
    }

    public void addComment(Comment comment) {
        this.commentList.add(comment);
    }

    public void updateOperator(Operator operator) {
        this.operators.add(operator);
    }

    public void update(String cardName, String cardDescription, String color, String dueDate) {
        this.cardName = cardName;
        this.cardDescription = cardDescription;
        this.cardColor = color;
        this.dueDate = dueDate;
    }

    public void updateCol(Col newCol) {
        this.col = newCol;
    }
}
