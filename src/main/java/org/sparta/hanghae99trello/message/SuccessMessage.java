package org.sparta.hanghae99trello.message;

public enum SuccessMessage {
    JOIN_SUCCESS_MESSAGE("회원가입이 완료되었습니다."),
    LOGIN_SUCCESS_MESSAGE("로그인이 완료되었습니다."),
    DELETE_SUCCESS_MESSAGE("삭제가 완료되었습니다."),
    UPDATE_USER_SUCCESS_MESSAGE("사용자 정보가 수정되었습니다."),
    CREATE_BOARD_SUCCESS_MESSAGE("보드 생성이 완료되었습니다."),
    UPDATE_BOARD_SUCCESS_MESSAGE("보드 수정이 완료되었습니다.");

    private final String successMessage;

    SuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getSuccessMessage() {
        return "[SUCCESS] " + successMessage;
    }
}