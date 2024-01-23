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

import java.util.List;
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
        RLock boardLock = boardService.createBoardLock(boardId);

        try {
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
                boardLock.unlock();
            }
        }

    public List<ColResponseDto> getCols(Long boardId) {
        List<Col> cols = colRepository.findByBoardId(boardId);

        return cols.stream()
                .map(ColResponseDto::new)
                .collect(Collectors.toList());
    }

    public ColResponseDto updateCol(Long boardId, Long columnId, ColRequestDto requestDto) {
        RLock boardLock = boardService.createBoardLock(boardId);
        RLock colLock = createColLock(columnId);

        try {
            Board board = boardService.findBoard(boardId);
            Col col = findCol(columnId);

            if (!col.getBoard().getId().equals(boardId)) {
                throw new IllegalArgumentException(ErrorMessage.ID_MISMATCH_ERROR_MESSAGE.getErrorMessage());
            }

            col.setColName(requestDto.getColName());
            return new ColResponseDto(colRepository.save(col));
        } finally {
            colLock.unlock();
            boardLock.unlock();
        }
    }



    public void deleteCol(Long boardId, Long columnId) {
        RLock boardLock = boardService.createBoardLock(boardId);
        RLock colLock = createColLock(columnId);

        try {
            Board board = boardService.findBoard(boardId);
            Col col = findCol(columnId);

            if (!col.getBoard().getId().equals(board.getId())) {
                throw new IllegalArgumentException(ErrorMessage.ID_MISMATCH_ERROR_MESSAGE.getErrorMessage());
            }

            colRepository.deleteById(columnId);
        } finally {
            colLock.unlock();
            boardLock.unlock();
        }

    }

    @Transactional
    public ColResponseDto updateColIdx(Long boardId, Long columnId, Long columnOrderIndex) {
        RLock boardLock = boardService.createBoardLock(boardId);
        RLock colLock = createColLock(columnId);

        try {

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
            colLock.unlock();
            boardLock.unlock();
        }
    }

    public Col findCol(Long id) {
        return colRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException(ErrorMessage.NOT_EXIST_COL_ERROR_MESSAGE.getErrorMessage()));
    }

    public RLock createColLock(Long columnId) {
        String lockKey = "ColLock"+ columnId.toString();
        RLock lock = redissonClient.getLock(lockKey);

        if (!lock.tryLock()) {
            throw new RuntimeException(ErrorMessage.LOCK_NOT_ACQUIRED_ERROR_MESSAGE.getErrorMessage());
        }
        return lock;
    }
}

