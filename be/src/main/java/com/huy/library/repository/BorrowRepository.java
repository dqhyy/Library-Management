package com.huy.library.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.huy.library.entity.Borrow;
import com.huy.library.enums.BorrowStatus;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    // R4: user đang mượn bao nhiêu cuốn
    long countByUserIdAndStatus(Long userId, BorrowStatus status);

    // R6: user đã mượn đúng cuốn này mà chưa trả
    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, BorrowStatus status);

    // R5: user có khoản mượn quá hạn (đang mượn và hạn trả < now)
    boolean existsByUserIdAndStatusAndDueDateBefore(Long userId, BorrowStatus status, LocalDateTime now);

    // Danh sách mọi khoản quá hạn (JPQL)
    @Query("""
        select b from Borrow b
        join fetch b.user
        join fetch b.book
        where b.status = :status and b.dueDate < :now
        """)
    List<Borrow> findOverdue(@Param("status") BorrowStatus status, @Param("now") LocalDateTime now);

    List<Borrow> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "book"})
    List<Borrow> findByUserIdOrderByBorrowedAtDesc(Long userId);
}