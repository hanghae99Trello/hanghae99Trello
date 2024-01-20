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

            Board board = boardService.findBoard(boardId);

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

    public ColResponseDto updateCol(Long boardId, Long columnId, ColRequestDto requestDto) {
        String lockKey = "ColLock";

        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock()) {
                throw new RuntimeException(ErrorMessage.LOCK_NOT_ACQUIRED_ERROR_MESSAGE.getErrorMessage());
            }

            lock.lock();

            Board board = boardService.findBoard(boardId);

            Col col = findCol(columnId);

            if (!col.getBoard().getId().equals(boardId)) {
                throw new IllegalArgumentException(ErrorMessage.ID_MISMATCH_ERROR_MESSAGE.getErrorMessage());
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

            Board board = boardService.findBoard(boardId);

            Col col = findCol(columnId);

            if (!col.getBoard().getId().equals(board.getId())) {
                throw new IllegalArgumentException(ErrorMessage.ID_MISMATCH_ERROR_MESSAGE.getErrorMessage());
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

            Board board = boardService.findBoard(boardId);

            Col columnToUpdate = findCol(columnId);

            if (!columnToUpdate.getBoard().getId().equals(boardId)) {
                throw new IllegalArgumentException(ErrorMessage.ID_MISMATCH_ERROR_MESSAGE.getErrorMessage());
            }

            List<Col> colList = board.getColList();

            Long currentIndex = columnToUpdate.getColIndex();

            colList.remove(columnToUpdate);

            columnToUpdate.setColIndex(columnOrderIndex);

            for (Col col : colList) {
                if (!col.getId().equals(columnId) && col.getColIndex() >= columnOrderIndex) {
                    col.setColIndex(col.getColIndex() + 1);
                }
            }

            colList.add(columnToUpdate);
            List<Col> savedCols = colRepository.saveAll(colList);

            return new ColResponseDto(columnToUpdate);
        } finally {
            lock.unlock();
        }
    }

    public Col findCol(Long id) {
        return colRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException(ErrorMessage.EXIST_COL_ERROR_MESSGAGE.getErrorMessage()));
    }
}
