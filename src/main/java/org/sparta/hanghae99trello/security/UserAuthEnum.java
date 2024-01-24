package org.sparta.hanghae99trello.security;

public enum UserAuthEnum {

    USER(Authority.USER);

    private final String authority;

    UserAuthEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String USER = "AUTH_USER";
    }
}