package org.sparta.hanghae99trello.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sparta.hanghae99trello.security.UserAuthEnum;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(nullable = false)
    private String name;

    @jakarta.persistence.Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @jakarta.persistence.Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserAuthEnum auth;

    public User(String name, String email, String password, UserAuthEnum auth) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.auth = auth;
    }
}