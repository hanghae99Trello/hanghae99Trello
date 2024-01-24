package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserRequestDto {

    private String name;
    private String email;
    private String password;
    private String phone;
    private String auth;

    public UserRequestDto(String name, String email, String password, String phone, String auth) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.auth = auth;
    }
}
