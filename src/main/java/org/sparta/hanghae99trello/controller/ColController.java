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
public class ColController {

    private final ColService colService;

    // TODO : mapping주소는 나중에 바꾸자
    // Question : boardId를 받으려면 일단은 Col에서 BoardId를 가져야할텐데
    // 다대일로 매핑해야겠지? 보드(다) - 컬럼(일)
    // 순환참조는 안 되겠지?
    // 일단 getCols에서 boardId를 받으면 ColsRepostory에서 findbyboardId로 찾도록 하자
    // PostMapping의 경우엔 RequestParam과 RequestBody 두가지 방식으로
    // 한 번에 받을 수는 없다고 하니 (할 수 있을 수도?) mapping을 바꾸는 방식도 고려해보자.

    @GetMapping("/users/boards/{boardId}/columns")
    public List<ColResponseDto> getCols(@PathVariable Long boardId) {
        return colService.getCols(boardId);
    }

    @PostMapping("/users/boards/{boardId}/columns")
    public void CreateCol(@PathVariable Long boardId, @RequestBody ColRequestDto requestDto) {
        colService.createCol(boardId, requestDto);
    }

    @PutMapping("/users/boards/{boardId}/columns/{columnId}")
    public void updateCol() {

    }

    @DeleteMapping("/users/boards/{boardId}/columns/{columnId}")
    public void deleteCol() {

    }

    @PutMapping("/users/boards/{boardId}/columns/{columnId}/{columnOrderIndex}")
    public void updateColIdx() {
    }
}
