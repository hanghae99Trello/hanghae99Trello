package org.sparta.hanghae99trello.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.sparta.hanghae99trello.dto.ColRequestDto;
import org.sparta.hanghae99trello.dto.ColResponseDto;
import org.sparta.hanghae99trello.entity.Board;
import org.sparta.hanghae99trello.entity.Col;
import org.sparta.hanghae99trello.message.ErrorMessage;
import org.sparta.hanghae99trello.repository.BoardRepository;
import org.sparta.hanghae99trello.repository.ColRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColService {
    public final ColRepository colRepository;
    public final BoardRepository boardRepository;
    public final RedissonClient redissonClient;
    public final BoardService boardService;

    @Transactional
    public ColResponseDto createCol(Long boardId, ColRequestDto requestDto) {
        String lockKey = "ColLock";

        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock()) {
                throw new RuntimeException(ErrorMessage.LOCK_NOT_ACQUIRED_ERROR_MESSAGE.getErrorMessage());
            }
            lock.lock();
            Optional<Board> optionalBoard = boardRepository.findById(boardId);

            if (optionalBoard.isEmpty()) {
                throw new IllegalArgumentException(ErrorMessage.EXIST_BOARD_ERROR_MESSAGE.getErrorMessage());
            }

            Board board = optionalBoard.get();
//            Board board = boardService.getBoardById(boardId);
            Long lastColIndex = colRepository.findLastColIndexByBoardId(boardId);

            Long newColIndex = (lastColIndex != null) ? lastColIndex + 1 : 1;

            Col col = new Col(
                    requestDto.getColName(),
                    newColIndex,
                    board
            );
            Col savedCol = colRepository.save(col);

            return new ColResponseDto(savedCol);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    public List<ColResponseDto> getCols(Long boardId) {
        List<Col> cols = colRepository.findByBoardId(boardId);

        return cols.stream()
                .map(ColResponseDto::new)
                .collect(Collectors.toList());
    }

    // TODO : Error Handling (if board is not present, boardId랑 Column이 속한 ID 다른경우! )

    public ColResponseDto updateCol(Long boardId, Long columnId, ColRequestDto requestDto) {
        String lockKey = "ColLock";

        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock()) {
                throw new RuntimeException(ErrorMessage.LOCK_NOT_ACQUIRED_ERROR_MESSAGE.getErrorMessage());
            }

            lock.lock();

            boardRepository.findById(boardId).orElseThrow(() -> new RuntimeException(ErrorMessage.EXIST_BOARD_ERROR_MESSAGE.getErrorMessage()));

            Col col = findCol(columnId);

            if (!col.getBoard().getId().equals(boardId)) {
                throw new RuntimeException(ErrorMessage.ID_MISMATCH_ERROR_MESSAGE.getErrorMessage());
            }

            col.setColName(requestDto.getColName());

            return new ColResponseDto(colRepository.save(col));
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
    }

    public void deleteCol(Long boardId, Long columnId) {
        String lockkey = "ColLock";

        RLock lock = redissonClient.getLock(lockkey);

        try {
            if (!lock.tryLock()) {
                throw new RuntimeException(ErrorMessage.LOCK_NOT_ACQUIRED_ERROR_MESSAGE.getErrorMessage());
            }

            lock.lock();

            Optional<Board> optionalBoard = boardRepository.findById(boardId);
            if (optionalBoard.isEmpty()) {
                throw new RuntimeException(ErrorMessage.EXIST_BOARD_ERROR_MESSAGE.getErrorMessage());
            }


            Board board = optionalBoard.get();
            Col col = findCol(columnId);

            if (!col.getBoard().getId().equals(board.getId())) {
                throw new RuntimeException(ErrorMessage.ID_MISMATCH_ERROR_MESSAGE.getErrorMessage());
            }

            colRepository.deleteById(columnId);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

    @Transactional
    public ColResponseDto updateColIdx(Long boardId, Long columnId, Long columnOrderIndex) {
        String lockKey = "ColLock";

        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock()) {
                throw new RuntimeException(ErrorMessage.LOCK_NOT_ACQUIRED_ERROR_MESSAGE.getErrorMessage());
            }

            lock.lock();

            Optional<Board> optionalBoard = boardRepository.findById(boardId);
            if (optionalBoard.isEmpty()) {
                throw new RuntimeException(ErrorMessage.EXIST_BOARD_ERROR_MESSAGE.getErrorMessage());
            }

            Col columnToUpdate = findCol(columnId);

            if (!columnToUpdate.getBoard().getId().equals(boardId)) {
                throw new RuntimeException(ErrorMessage.ID_MISMATCH_ERROR_MESSAGE.getErrorMessage());
            }

            Board board = optionalBoard.get();
            List<Col> colList = board.getColList();

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
        } finally {

        }


    }

    public Col findCol(Long id) {
        return colRepository.findById(id).orElseThrow(() ->
                new RuntimeException(ErrorMessage.EXIST_COL_ERROR_MESSGAGE.getErrorMessage()));
    }
}
