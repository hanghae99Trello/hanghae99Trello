package org.sparta.hanghae99trello.repository;

import org.sparta.hanghae99trello.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByName(String name);
    User findByEmail(String email);
}
