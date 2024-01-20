package org.sparta.hanghae99trello.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.Column;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.sparta.hanghae99trello.dto.UserRequestDto;
import org.sparta.hanghae99trello.security.UserAuthEnum;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserAuthEnum auth;

    @OneToMany(mappedBy = "createdBy")
    private Set<Board> createdBoards;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Participant> boards;

    public User(String name, String email, String password, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.auth = UserAuthEnum.valueOf("USER");
    }

    public void update(UserRequestDto requestDto) {
        this.name = requestDto.getName();
        this.email = requestDto.getEmail();
        this.phone = requestDto.getPhone();
    }
}