package org.sparta.hanghae99trello.controller;

import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.dto.ColRequestDto;
import org.sparta.hanghae99trello.dto.ColResponseDto;
import org.sparta.hanghae99trello.service.BoardService;
import org.sparta.hanghae99trello.service.ColService;
import org.sparta.hanghae99trello.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cols")
public class ColController {
    private final UserService userService;
    private final BoardService boardService;
    private final ColService colService;

    // TODO : mapping주소는 나중에 바꾸자

    @GetMapping("/유저ㅓㅓ/보오으드/보드ID/columns")
    public List<ColResponseDto> getCols(@RequestBody ColRequestDto colRequestDto) {
        return null;
    }

    @PostMapping("/유저ㅓㅓ/보오으드/보드ID/columns")
    public void CreateCols() {

    }

    @PutMapping("/유저ㅓㅓ/보오으드/보드ID/columns/컬럼ID")
    public void updateCols() {

    }

    @DeleteMapping("/유저ㅓㅓ/보오으드/보드ID/columns")
    public void deleteCols() {

    }

    @PutMapping("/유저ㅓㅓ/보오으드/보드ID/columns/컬럼ID/컬럼순서idx")
    public void updateColsIdx() {
    }
}
