package org.sparta.hanghae99trello.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hey")
    public String hey() {
        return "어디갔다가 이제왔어 ㅋㅋ";
    }
}
