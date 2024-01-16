package org.sparta.hanghae99trello.service;

import org.sparta.hanghae99trello.dto.ColRequestDto;
import org.sparta.hanghae99trello.dto.ColResponseDto;
import org.sparta.hanghae99trello.entity.Board;
import org.sparta.hanghae99trello.entity.Col;
import org.sparta.hanghae99trello.repository.BoardRepository;
import org.sparta.hanghae99trello.repository.ColRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ColService {
    public final ColRepository colRepository;
    public final BoardRepository boardRepository;

    public ColService(ColRepository colRepository, BoardRepository boardRepository) {
        this.colRepository = colRepository;
        this.boardRepository = boardRepository;
    }

    @Transactional
    public void createCol(Long boardId, ColRequestDto requestDto) {
        Optional<Board> optionalBoard = boardRepository.findById(boardId);

        // TODO : 에러 처리
//        if (!optionalBoard.isPresent()) {
//            // Handle the case where the board with the specified ID is not found
//            throw new BoardNotFoundException("Board with ID " + boardId + " not found");
//
//        } else {
        Board board = optionalBoard.get();

        // Create and save the new Col
        Col col = new Col(
                requestDto.getColName(),
                requestDto.getColIndex(),
                board
        );

        Col savedCol = colRepository.save(col);

        // Create and return the response DTO
        new ColResponseDto(savedCol);
//        }
    }


    public List<ColResponseDto> getCols(Long boardId) {
        List<Col> cols = colRepository.findByBoardId(boardId);

        // Convert the list of Col entities to a list of ColResponseDto
        return cols.stream()
                .map(ColResponseDto::new)
                .collect(Collectors.toList());
    }

    public void updateCol(Long boardId, Long columnId, ColRequestDto requestDto) {

        // TODO : Error Handling (if board is not present, boardId랑 Column이 속한 ID 다른경우! )
        Optional<Board> optionalBoard = boardRepository.findById(boardId);
        Optional<Col> optionalCol = colRepository.findById(columnId);

        Board board = optionalBoard.get();
        Col col = optionalCol.get();

        col.setColName(requestDto.getColName());

        colRepository.save(col);
    }

    public void deleteCol(Long boardId, Long columnId) {
        Optional<Board> optionalBoard = boardRepository.findById(boardId);
        Optional<Col> optionalCol = colRepository.findById(columnId);

        if (optionalBoard.isPresent() && optionalCol.isPresent()) {
            Board board = optionalBoard.get();
            Col col = optionalCol.get();

            // Check if the column belongs to the specified board
            if (col.getBoard().getId().equals(board.getId())) {
                // Delete the column
                colRepository.deleteById(columnId);
            } else {
                // Error handling: The specified column does not belong to the specified board
                throw new IllegalArgumentException("Column with ID " + columnId +
                        " does not belong to the board with ID " + boardId);
            }
        } else {
            // Error handling: Either the board or the column is not present
            throw new IllegalArgumentException("Board or Column not found");
        }
    }
}
