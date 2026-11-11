package com.huy.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.huy.library.entity.Category;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Category> findByNameIgnoreCase(String name);

}