package org.sparta.hanghae99trello.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sparta.hanghae99trello.dto.BoardRequestDto;
import org.sparta.hanghae99trello.security.UserAuthEnum;

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
    @JsonIgnore
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private Set<Participant> participants;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Col> colList;

    public Board(String boardName, String boardColor, String boardDescription, User createdBy, Set<Participant> participants, List<Col> colList) {
        this.boardName = boardName;
        this.boardColor = boardColor;
        this.boardDescription = boardDescription;
        this.createdBy = createdBy;
        this.participants = participants;
        this.colList = colList;
    }
}
