package org.sparta.hanghae99trello.service;

import org.sparta.hanghae99trello.dto.BoardResponseDto;
import org.sparta.hanghae99trello.dto.ColResponseDto;
import org.sparta.hanghae99trello.entity.Col;
import org.sparta.hanghae99trello.repository.ColRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColService {
    public final ColRepository colRepository;


    public ColService(ColRepository colRepository) {
        this.colRepository = colRepository;
    }

    public List<ColResponseDto> getCols() {
        List<Col> cols = colRepository.findAll();

        // Convert the list of Col entities to a list of ColResponseDto
        return cols.stream()
                .map(ColResponseDto::new)
                .collect(Collectors.toList());
    }

}
