package org.sparta.hanghae99trello.repository;

import org.sparta.hanghae99trello.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByName(String name);

    User findByEmail(String email);
}
