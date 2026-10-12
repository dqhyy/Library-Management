package com.huy.library.entity;

import java.time.LocalDateTime;

import com.huy.library.enums.BorrowStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "borrows", 
        indexes = { @Index(name = "idx_borrows_user_status", columnList = "user_id, status"),
                    @Index(name = "idx_borrows_due_date", columnList = "due_date") }
)
@Getter 
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Borrow extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private  Book book;

    @Column(name = "borrowed_at", nullable = false)
    private LocalDateTime borrowedAt;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BorrowStatus status;

    public Borrow(User user, Book book, LocalDateTime borrowedAt, int loanDays){
        this.user = user;
        this.book = book;
        this.borrowedAt = borrowedAt;
        this.dueDate = borrowedAt.plusDays(loanDays);
        this.status = BorrowStatus.BORROWING;

    }
// Ko dùng setter, trạng thái status qua markReturned
    public void markReturned(LocalDateTime returnedAt) {
        if (this.status != BorrowStatus.BORROWING) {
            throw new IllegalStateException("Khoản mượn này đã được trả rồi");
        }
        this.returnedAt = returnedAt;
        this.status = BorrowStatus.RETURNED;
    }

    public boolean isOverdue(LocalDateTime now) {
        return this.status == BorrowStatus.BORROWING && now.isAfter(this.dueDate);
    }

    public boolean isReturnedLate() {
        return this.status == BorrowStatus.RETURNED
                && this.returnedAt != null
                && this.returnedAt.isAfter(this.dueDate);
    }
}
