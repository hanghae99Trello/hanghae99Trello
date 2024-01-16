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

    @Column(name = "col_index")
    private Long colIndex;


    @ManyToOne
    @JoinColumn(name = "board_id") // Specify the foreign key column
    private Board board;

    public Col(String colName, Long colIndex, Board board) {

        this.colName = colName;
        this.colIndex = colIndex;
        this.board = board;
    }

}
