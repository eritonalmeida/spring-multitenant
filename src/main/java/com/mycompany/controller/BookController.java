package com.mycompany.controller;

import com.mycompany.entity.Book;
import com.mycompany.repository.BookRepository;
import com.mycompany.service.CustomUserDetails;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    @Autowired
    private BookRepository repository;

    @GetMapping("/")
    public List<Book> listAll(UsernamePasswordAuthenticationToken auth) {

        var user = (CustomUserDetails) auth.getPrincipal();

        var list = repository.findAll();

        if (!list.isEmpty()) {
            return list;
        }

        var book = new Book();

        book.setTitle("O Código Da Vinci");

        if (user.getTenantId().equals("db2")) {
            book.setTitle("Anjos e Demônios");
        }

        repository.save(book);

        return repository.findAll();
    }

}
