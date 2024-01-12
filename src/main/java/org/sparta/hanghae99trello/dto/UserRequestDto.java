package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserRequestDto {
    private String name;
    private String email;
    private String password;
    private String auth;

    public UserRequestDto(String name, String email, String password, String auth) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.auth = auth;
    }
}
