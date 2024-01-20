package org.sparta.hanghae99trello.service;

import org.sparta.hanghae99trello.dto.ColRequestDto;
import org.sparta.hanghae99trello.dto.ColResponseDto;
import org.sparta.hanghae99trello.entity.Board;
import org.sparta.hanghae99trello.entity.Col;
import org.sparta.hanghae99trello.repository.BoardRepository;
import org.sparta.hanghae99trello.repository.ColRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
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
    public ColResponseDto createCol(Long boardId, ColRequestDto requestDto) {
        Optional<Board> optionalBoard = boardRepository.findById(boardId);

        if (optionalBoard.isPresent()) {
            Board board = optionalBoard.get();
            Long lastColIndex = colRepository.findLastColIndexByBoardId(boardId);

            // Increment the colIndex
            Long newColIndex = (lastColIndex != null) ? lastColIndex + 1 : 1;

            Col col = new Col(
                    requestDto.getColName(),
                    newColIndex,
                    board
            );

            Col savedCol = colRepository.save(col);

            // Create and return the response DTO
            return new ColResponseDto(savedCol);
        } else {
            // Handle the case where the board with the specified ID is not found
//            throw new BoardNotFoundException("Board with ID " + boardId + " not found");
        }
        return null;
    }


    public List<ColResponseDto> getCols(Long boardId) {
        List<Col> cols = colRepository.findByBoardId(boardId);

        // Convert the list of Col entities to a list of ColResponseDto
        return cols.stream()
                .map(ColResponseDto::new)
                .collect(Collectors.toList());
    }

    public ColResponseDto updateCol(Long boardId, Long columnId, ColRequestDto requestDto) {

        // TODO : Error Handling (if board is not present, boardId랑 Column이 속한 ID 다른경우! )
        Optional<Col> optionalCol = colRepository.findById(columnId);

        Col col = optionalCol.get();

        col.setColName(requestDto.getColName());

        return new ColResponseDto(colRepository.save(col));
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

    @Transactional
    public ColResponseDto updateColIdx(Long boardId, Long columnId, Long columnOrderIndex) {
        Optional<Board> optionalBoard = boardRepository.findById(boardId);

        if (optionalBoard.isEmpty()) {
            // Handle the case where the board with the given id is not found
            return null; // or handle it based on your application's requirements
        }

        Board board = optionalBoard.get();
        List<Col> colList = board.getColList();

        Optional<Col> optionalCol = colList.stream()
                .filter(col -> col.getId().equals(columnId))
                .findFirst();

        if (optionalCol.isEmpty()) {
            // Handle the case where the column with the given id is not found
            return null; // or handle it based on your application's requirements
        }

        Col columnToUpdate = optionalCol.get();

        // Get the current index of the column
        Long currentIndex = columnToUpdate.getColIndex();

        // Remove the column from the list to prevent duplication
        colList.remove(columnToUpdate);

        // Update the column index
        columnToUpdate.setColIndex(columnOrderIndex);

        // Adjust other indices to prevent conflicts
        for (Col col : colList) {
            if (!col.getId().equals(columnId) && col.getColIndex() >= columnOrderIndex) {
                // Increment indices of columns with indices greater than or equal to the updated column index
                col.setColIndex(col.getColIndex() + 1);
            }
        }

        // Re-add the column to the list at the correct position
        colList.add(columnToUpdate);

        // Save the changes back to the database
        List<Col> savedCols = colRepository.saveAll(colList);

        // Return the response DTO for the updated column
        return new ColResponseDto(columnToUpdate);
    }
}
