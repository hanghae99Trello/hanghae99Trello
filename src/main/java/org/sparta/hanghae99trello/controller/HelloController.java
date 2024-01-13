package org.sparta.hanghae99trello.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hey")
    public String hello() {
        return "hey 제발 쫌!!!!!!!!!!!!!!!!!";
    }
}
  