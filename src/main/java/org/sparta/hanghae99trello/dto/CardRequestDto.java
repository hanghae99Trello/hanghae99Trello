package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CardRequestDto {

    private String cardName;
    private String cardDescription;
    private String color;
    private List<String> operatorNames;
    private String dueDate;
}
