package com.huy.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "authors", indexes = @Index(name = "idx_authors_name", columnList = "name"))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Author extends BaseEntity{

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    @Builder 
    public Author(String name, String biography){
        this.name = name;
        this.biography = biography;
    }
}
