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
    }

    public List<ColResponseDto> getCols(Long boardId) {
        List<Col> cols = colRepository.findByBoardId(boardId);

        return cols.stream()
                .map(ColResponseDto::new)
                .collect(Collectors.toList());
    }

    public ColResponseDto updateCol(Long boardId, Long columnId, ColRequestDto requestDto) {
        RLock colLock = createColLock(columnId);

        try {
            Col col = findCol(columnId);

            isBoardIdMatch(boardId, col);

            col.setColName(requestDto.getColName());
            return new ColResponseDto(colRepository.save(col));
        } finally {
            colLock.unlock();
        }
    }

    public void deleteCol(Long boardId, Long columnId) {
        RLock colLock = createColLock(columnId);

        try {
            Col col = findCol(columnId);

            isBoardIdMatch(boardId, col);

            colRepository.deleteById(columnId);
        } finally {
            colLock.unlock();
        }
    }

    @Transactional
    public ColResponseDto updateColIdx(Long boardId, Long columnId, Long columnOrderIndex) {
        RLock colLock = createColLock(columnId);

        try {
            Board board = boardService.findBoard(boardId);
            Col columnToUpdate = findCol(columnId);

            isBoardIdMatch(boardId, columnToUpdate);

            List<Col> colList = board.getColList();
            colList.remove(columnToUpdate);
            columnToUpdate.setColIndex(columnOrderIndex);

            for (Col col : colList) {
                if (!col.getId().equals(columnId) && col.getColIndex() >= columnOrderIndex) {
                    col.setColIndex(col.getColIndex() + 1);
                }
            }

            colList.add(columnToUpdate);
            colRepository.saveAll(colList);

            return new ColResponseDto(columnToUpdate);
        } finally {
            colLock.unlock();
        }
    }

    private Col findCol(Long id) {
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

    private static void isBoardIdMatch(Long boardId, Col columnToUpdate) {
        if (!columnToUpdate.getBoard().getId().equals(boardId)) {
            throw new IllegalArgumentException(ErrorMessage.ID_MISMATCH_ERROR_MESSAGE.getErrorMessage());
        }
    }
}

