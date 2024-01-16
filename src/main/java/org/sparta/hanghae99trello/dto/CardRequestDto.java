package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CardRequestDto {
    private String cardName;
    private String cardDescription;
    private String color;
    private List<Long> operatorIds;
    private LocalDate dueDate;
}
