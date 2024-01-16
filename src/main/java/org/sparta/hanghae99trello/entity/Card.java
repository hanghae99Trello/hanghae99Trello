package org.sparta.hanghae99trello.entity;

import com.fasterxml.jackson.annotation.*;
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

    @ManyToMany
    @JoinTable(
            name = "operator",
            joinColumns =  @JoinColumn(name = "card_ids"),
            inverseJoinColumns = @JoinColumn(name = "user_ids")
    )
    private List<User> operators;

    @OneToMany(mappedBy = "card",cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<Comment> commentList;

    @Column(nullable = true)
    private LocalDate dueDate;

    @Setter
    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "previous_card_id")
    private Card previousCard;

    @Setter
    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "next_card_id")
    private Card nextCard;

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
