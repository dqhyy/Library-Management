package com.huy.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.huy.library.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long>{
    List<Author> findByNameContainingIgnoreCase(String keyword);
}
