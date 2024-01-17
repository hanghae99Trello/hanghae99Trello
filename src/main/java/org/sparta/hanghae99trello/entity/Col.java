package org.sparta.hanghae99trello.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @JoinColumn(name = "first_card_id")
    private Long firstCardId;

    @JoinColumn(name = "last_card_id")
    private Long lastCardId;

    public Long addCard(Card card) {
        if (this.firstCardId == null) {
            this.firstCardId = card.getId();
            this.lastCardId = card.getId();
            return card.getId();
        }
        Long prev_id = lastCardId;
        this.lastCardId = card.getId();
        return prev_id;
    }

    public Col(String colName, Long colIndex, Board board) {

        this.colName = colName;
        this.colIndex = colIndex;
        this.board = board;
    }

    public Boolean deleteCard(Card card) {
        if (this.firstCardId.equals(this.lastCardId) && this.firstCardId.equals(card.getId())) {
            this.firstCardId = null;
            this.lastCardId = null;
            return true;
        } else if (this.firstCardId.equals(card.getId())) {
            this.firstCardId = card.getNextCardId();
        } else if (this.lastCardId.equals(card.getId())) {
            this.lastCardId = card.getPreviousCardId();
        }
        return false;
    }

}
