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
@Setter
@NoArgsConstructor
@Table(name = "cols")
public class Col {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "col_name")
    private String colName;

    @Column(name = "col_index")
    private Long colIndex;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "board")
    private Board board;

    @OneToMany(mappedBy = "col", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Card> cardList;

    public Col(String colName, Long colIndex, Board board) {
        this.colName = colName;
        this.colIndex = colIndex;
        this.board = board;
        this.cardList = new ArrayList<>();
    }

    public void addCard(Card card) {
        this.cardList.add(card);
    }

    public void deleteCard(Card card) {
        this.cardList.remove(card);
    }
}
