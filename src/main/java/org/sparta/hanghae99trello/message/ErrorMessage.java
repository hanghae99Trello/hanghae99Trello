package org.sparta.hanghae99trello.message;

public enum ErrorMessage {
    EXIST_TOKEN_ERROR_MESSAGE("토큰을 찾을 수 없습니다."),
    EXIST_USER_ERROR_MESSAGE("존재하지 않는 사용자입니다."),
    EXIST_BOARD_ERROR_MESSAGE("존재하지 않는 보드입니다."),
    INVALID_JWT_ERROR_MESSAGE("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다."),
    EXPIRED_JWT_ERROR_MESSAGE("Expired JWT token, 만료된 JWT token 입니다."),
    UNSUPPORTED_JWT_ERROR_MESSAGE("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다."),
    EMPTY_JWT_ERROR_MESSAGE("JWT claims is empty, 잘못된 JWT 토큰 입니다."),
    EMAIL_FORMAT_ERROR_MESSAGE("올바른 이메일 형식이 아닙니다."),
    PASSWORD_VALIDATION_ERROR_MESSAGE("비밀번호는 최소 8자 이상, 15자 이하이며 알파벳 대소문자, 숫자, 특수문자로 구성되어야 합니다."),
    PASSWORD_MISMATCH_ERROR_MESSAGE("로그인에 실패하였습니다."),
    AUTH_EXCEPTION_MESSAGE("로그인 해야 가능한 기능입니다."),
    UPDATE_BOARD_AUTH_ERROR_MESSAGE("자신이 만든 보드만 수정할 수 있습니다."),
    DELETE_BOARD_AUTH_ERROR_MESSAGE("자신이 만든 보드만 삭제할 수 있습니다."),
    UPDATE_USER_AUTH_ERROR_MESSAGE("본인만 수정할 수 있습니다."),
    DELETE_USER_AUTH_ERROR_MESSAGE("본인만 삭제할 수 있습니다.");


    private final String errorMessage;

    ErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return "[ERROR] " + errorMessage;
    }
}


