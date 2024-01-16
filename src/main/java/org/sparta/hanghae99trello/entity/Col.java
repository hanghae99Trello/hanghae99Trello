package org.sparta.hanghae99trello.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "col_Index")
    private Long colIndex;

    @OneToOne
    @JoinColumn(name="first_card_id")
    private Card firstCard;

    @OneToOne
    @JoinColumn(name="last_card_id")
    private Card lastCard;

    public void addCard(Card card) {
        if (this.firstCard == null){
            this.firstCard = card;
        }
        else {
            this.lastCard.setNextCard(card);
            card.setPreviousCard(this.lastCard);
        }
        this.lastCard = card;
    }

    public void deleteCard(Card card) {
        if (this.firstCard == this.lastCard && this.firstCard == card){
            firstCard = null;
            lastCard = null;
        }
        else if(this.firstCard == card){
            Card nxt_card = card.getNextCard();
            nxt_card.setPreviousCard(null);
            this.firstCard = nxt_card;

        }
        else if(this.lastCard == card){
            Card pre_card = card.getPreviousCard();
            pre_card.setNextCard(null);
            this.lastCard = pre_card;
        }
        else{
            Card pre_card = card.getPreviousCard();
            Card nxt_card = card.getNextCard();
            pre_card.setNextCard(nxt_card);
            nxt_card.setPreviousCard(pre_card);
        }
    }

//    @OneToMany(mappedBy = "col", cascade = CascadeType.ALL)
//    private List<Card> cards;
}
