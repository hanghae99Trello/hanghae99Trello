package org.sparta.hanghae99trello.service;

import org.sparta.hanghae99trello.repository.ColRepository;
import org.springframework.stereotype.Service;

@Service
public class ColService {
    public final ColRepository colRepository;


    public ColService(ColRepository colRepository) {
        this.colRepository = colRepository;
    }
}
