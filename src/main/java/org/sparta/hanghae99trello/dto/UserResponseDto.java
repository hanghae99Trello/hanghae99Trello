package org.sparta.hanghae99trello.dto;

import lombok.Getter;
import org.sparta.hanghae99trello.entity.User;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String name;
    private final String email;
    private final String auth;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.auth = user.getAuth().getAuthority();
    }
}
