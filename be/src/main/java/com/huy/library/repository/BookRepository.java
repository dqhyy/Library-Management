package com.huy.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.huy.library.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);

    @EntityGraph(attributePaths = { "authors", "categories" })
    Optional<Book> findWithDetailsById(Long id);
}
