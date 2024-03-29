package org.sparta.hanghae99trello.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_name")
    private String boardName;

    @Column(name = "board_color")
    private String boardColor;

    @Column(name = "board_description")
    private String boardDescription;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Participant> participants;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Col> colList = new ArrayList<>();

    public Board(String boardName, String boardColor, String boardDescription, User createdBy, Set<Participant> participants) {
        this.boardName = boardName;
        this.boardColor = boardColor;
        this.boardDescription = boardDescription;
        this.createdBy = createdBy;
        this.participants = participants;
    }

    public List<Col> getColListSort() {
        List<Col> colList = this.getColList();
        for (Col col : colList) {
            List<Card> cardList = col.getCardList();
            cardList.sort(new Comparator<Card>() {
                @Override
                public int compare(Card card1, Card card2) {
                    return Double.compare(card1.getOrderIndex(), card2.getOrderIndex());
                }
            });
        }
        return colList;
    }
}
