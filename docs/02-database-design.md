# Database Design

## 1. Database Overview

### 1.1. Database

```text
Database: library_management
DBMS: MySQL
Engine: InnoDB
Charset: utf8mb4
```

Hệ thống sử dụng MySQL để lưu trữ dữ liệu cho Library Management System.

Database gồm các bảng chính:

```text
users
books
authors
categories
book_authors
book_categories
borrows
```

---

# 2. Database Design Principles

## 2.1. Naming Convention

Sử dụng `snake_case` cho tên bảng và tên cột.

Ví dụ:

```text
available_quantity
published_date
created_at
updated_at
```

Tên bảng sử dụng dạng số nhiều:

```text
users
books
authors
categories
borrows
```

---

## 2.2. Primary Key

Các bảng chính sử dụng:

```sql
BIGINT AUTO_INCREMENT
```

Ví dụ:

```text
id BIGINT PRIMARY KEY AUTO_INCREMENT
```

Trong Java có thể ánh xạ thành:

```java
Long id;
```

và:

```java
@GeneratedValue(strategy = GenerationType.IDENTITY)
```

---

## 2.3. Foreign Key

Foreign Key được sử dụng để đảm bảo dữ liệu tham chiếu luôn hợp lệ.

Ví dụ:

```text
borrows.book_id → books.id
borrows.user_id → users.id
```

Database sẽ không cho phép tạo một `Borrow` tham chiếu đến User hoặc Book không tồn tại.

---

## 2.4. Unique Constraint

Sử dụng `UNIQUE` cho những dữ liệu không được phép trùng.

Các trường UNIQUE:

```text
users.username
users.email
books.isbn
categories.name
```

---

## 2.5. Nullable

Các trường bắt buộc sử dụng:

```text
NOT NULL
```

Các trường tùy chọn cho phép:

```text
NULL
```

Ví dụ:

```text
borrows.returned_at
```

được phép `NULL` vì khi sách chưa được trả thì chưa có thời điểm trả.

---

## 2.6. Timestamp

Các bảng chính sử dụng:

```text
created_at
updated_at
```

Kiểu dữ liệu:

```sql
DATETIME
```

Trong Java:

```java
LocalDateTime
```

---

## 2.7. Enum

Enum được lưu dưới dạng `VARCHAR` thay vì số thứ tự.

Ví dụ:

```text
ROLE_USER
ROLE_ADMIN
```

hoặc:

```text
ACTIVE
LOCKED
```

Không sử dụng `ORDINAL` vì nếu thay đổi thứ tự enum trong Java thì dữ liệu cũ có thể bị hiểu sai.

---

# 3. Entity Relationship

## 3.1. Relationship Overview

```text
User 1 ───────── N Borrow

Book 1 ───────── N Borrow

Book N ───────── N Author

Book N ───────── N Category
```

Chi tiết:

```text
users
   │
   │ 1
   │
   │ N
borrows
   │
   │ N
   │
   │ 1
books
  │  │
  │  └──────── N:N ──────── authors
  │
  └────────── N:N ───────── categories
```

---

# 4. Table Design

## 4.1. `users`

Lưu thông tin tài khoản người dùng.

### Structure

| Column       | Type         | Constraint         | Description                |
| ------------ | ------------ | ------------------ | -------------------------- |
| `id`         | BIGINT       | PK, AUTO_INCREMENT | ID người dùng              |
| `username`   | VARCHAR(50)  | NOT NULL, UNIQUE   | Tên đăng nhập              |
| `email`      | VARCHAR(100) | NOT NULL, UNIQUE   | Email                      |
| `password`   | VARCHAR(255) | NOT NULL           | BCrypt password hash       |
| `full_name`  | VARCHAR(100) | NOT NULL           | Họ tên                     |
| `phone`      | VARCHAR(20)  | NULL               | Số điện thoại              |
| `role`       | VARCHAR(20)  | NOT NULL           | `ROLE_USER` / `ROLE_ADMIN` |
| `status`     | VARCHAR(20)  | NOT NULL           | `ACTIVE` / `LOCKED`        |
| `created_at` | DATETIME     | NOT NULL           | Thời gian tạo              |
| `updated_at` | DATETIME     | NOT NULL           | Thời gian cập nhật         |

### SQL

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);
```

### Notes

`password` chỉ lưu password đã hash bằng BCrypt.

Không bao giờ lưu password dạng plaintext.

`role` sử dụng một cột duy nhất vì hệ thống hiện tại chỉ có:

```text
ROLE_USER
ROLE_ADMIN
```

và mỗi User chỉ có một Role.

---

# 5. `authors`

Lưu thông tin tác giả.

### Structure

| Column       | Type         | Constraint         | Description        |
| ------------ | ------------ | ------------------ | ------------------ |
| `id`         | BIGINT       | PK, AUTO_INCREMENT | ID tác giả         |
| `name`       | VARCHAR(150) | NOT NULL           | Tên tác giả        |
| `biography`  | TEXT         | NULL               | Tiểu sử            |
| `created_at` | DATETIME     | NOT NULL           | Thời gian tạo      |
| `updated_at` | DATETIME     | NOT NULL           | Thời gian cập nhật |

### SQL

```sql
CREATE TABLE authors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    biography TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);
```

### Note

`name` không đặt `UNIQUE`.

Lý do: hai tác giả khác nhau có thể có cùng tên.

---

# 6. `categories`

Lưu thông tin thể loại sách.

### Structure

| Column        | Type         | Constraint         | Description        |
| ------------- | ------------ | ------------------ | ------------------ |
| `id`          | BIGINT       | PK, AUTO_INCREMENT | ID thể loại        |
| `name`        | VARCHAR(100) | NOT NULL, UNIQUE   | Tên thể loại       |
| `description` | VARCHAR(255) | NULL               | Mô tả              |
| `created_at`  | DATETIME     | NOT NULL           | Thời gian tạo      |
| `updated_at`  | DATETIME     | NOT NULL           | Thời gian cập nhật |

### SQL

```sql
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);
```

---

# 7. `books`

Lưu thông tin đầu sách.

### Structure

| Column               | Type         | Constraint                  | Description            |
| -------------------- | ------------ | --------------------------- | ---------------------- |
| `id`                 | BIGINT       | PK, AUTO_INCREMENT          | ID sách                |
| `isbn`               | VARCHAR(20)  | NOT NULL, UNIQUE            | ISBN                   |
| `title`              | VARCHAR(255) | NOT NULL                    | Tên sách               |
| `description`        | TEXT         | NULL                        | Mô tả                  |
| `publisher`          | VARCHAR(150) | NULL                        | Nhà xuất bản           |
| `published_date`     | DATE         | NULL                        | Ngày xuất bản          |
| `quantity`           | INT          | NOT NULL, >= 0              | Tổng số bản            |
| `available_quantity` | INT          | NOT NULL, >= 0, <= quantity | Số bản còn có thể mượn |
| `cover_image`        | VARCHAR(500) | NULL                        | URL/path ảnh bìa       |
| `created_at`         | DATETIME     | NOT NULL                    | Thời gian tạo          |
| `updated_at`         | DATETIME     | NOT NULL                    | Thời gian cập nhật     |

### SQL

```sql
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    publisher VARCHAR(150),
    published_date DATE,
    quantity INT NOT NULL,
    available_quantity INT NOT NULL,
    cover_image VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,

    CONSTRAINT chk_books_quantity
        CHECK (quantity >= 0),

    CONSTRAINT chk_books_available_quantity
        CHECK (available_quantity >= 0),

    CONSTRAINT chk_books_available_lte_quantity
        CHECK (available_quantity <= quantity)
);
```

### Quantity vs Available Quantity

Hai trường này có ý nghĩa khác nhau:

```text
quantity
    ↓
Tổng số bản thư viện sở hữu

available_quantity
    ↓
Số bản hiện tại còn có thể cho mượn
```

Ví dụ:

```text
quantity = 10
available_quantity = 7
```

Có nghĩa:

```text
Thư viện sở hữu: 10 bản
Đang có thể cho mượn: 7 bản
Đang được mượn: 3 bản
```

Không tính `available_quantity` bằng cách `COUNT()` mỗi lần request.

Việc lưu trực tiếp `available_quantity` giúp:

* Kiểm tra nhanh.
* Dễ cập nhật khi mượn/trả.
* Có thể lock row `Book` khi xử lý concurrency.

Đổi lại, application phải đảm bảo giá trị này luôn đồng bộ với transaction.

---

# 8. `book_authors`

Đây là bảng trung gian cho quan hệ:

```text
Book N ───── N Author
```

### Structure

| Column      | Type   | Constraint |
| ----------- | ------ | ---------- |
| `book_id`   | BIGINT | PK, FK     |
| `author_id` | BIGINT | PK, FK     |

### SQL

```sql
CREATE TABLE book_authors (
    book_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,

    PRIMARY KEY (book_id, author_id),

    CONSTRAINT fk_book_authors_book
        FOREIGN KEY (book_id)
        REFERENCES books(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_book_authors_author
        FOREIGN KEY (author_id)
        REFERENCES authors(id)
        ON DELETE RESTRICT
);
```

### Why Composite Primary Key?

```text
PRIMARY KEY (book_id, author_id)
```

ngăn việc một Book được gắn cùng một Author nhiều lần.

Ví dụ:

```text
book_id = 1
author_id = 5
```

chỉ được xuất hiện một lần.

Không cần tạo thêm:

```text
id
```

vì bảng này chỉ lưu relationship và không có dữ liệu nghiệp vụ riêng.

---

# 9. `book_categories`

Đây là bảng trung gian cho quan hệ:

```text
Book N ───── N Category
```

### Structure

| Column        | Type   | Constraint |
| ------------- | ------ | ---------- |
| `book_id`     | BIGINT | PK, FK     |
| `category_id` | BIGINT | PK, FK     |

### SQL

```sql
CREATE TABLE book_categories (
    book_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,

    PRIMARY KEY (book_id, category_id),

    CONSTRAINT fk_book_categories_book
        FOREIGN KEY (book_id)
        REFERENCES books(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_book_categories_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON DELETE RESTRICT
);
```

### Delete Behavior

Nếu xóa Book:

```text
Book
  ↓
book_categories
  ↓
relationship bị xóa
```

Do đó:

```text
book_id → ON DELETE CASCADE
```

Nếu xóa Category đang được sử dụng:

```text
Category
  ↓
Book đang tham chiếu
```

Database từ chối thao tác:

```text
ON DELETE RESTRICT
```

Điều này tránh việc xóa Category làm mất relationship của Book một cách âm thầm.

---

# 10. `borrows`

Đây là bảng nghiệp vụ trung tâm của hệ thống.

Một record `borrow` đại diện cho:

```text
Một User
    +
Một Book
    +
Một lần mượn
```

### Structure

| Column        | Type        | Constraint         | Description              |
| ------------- | ----------- | ------------------ | ------------------------ |
| `id`          | BIGINT      | PK, AUTO_INCREMENT | ID khoản mượn            |
| `user_id`     | BIGINT      | NOT NULL, FK       | User mượn                |
| `book_id`     | BIGINT      | NOT NULL, FK       | Book được mượn           |
| `borrowed_at` | DATETIME    | NOT NULL           | Thời điểm mượn           |
| `due_date`    | DATETIME    | NOT NULL           | Hạn trả                  |
| `returned_at` | DATETIME    | NULL               | Thời điểm trả            |
| `status`      | VARCHAR(20) | NOT NULL           | `BORROWING` / `RETURNED` |
| `created_at`  | DATETIME    | NOT NULL           | Thời gian tạo            |
| `updated_at`  | DATETIME    | NOT NULL           | Thời gian cập nhật       |

### SQL

```sql
CREATE TABLE borrows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,

    borrowed_at DATETIME NOT NULL,
    due_date DATETIME NOT NULL,
    returned_at DATETIME NULL,

    status VARCHAR(20) NOT NULL,

    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,

    CONSTRAINT fk_borrows_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_borrows_book
        FOREIGN KEY (book_id)
        REFERENCES books(id)
        ON DELETE RESTRICT
);
```

---

# 11. Borrow Status

Database chỉ lưu hai trạng thái:

```text
BORROWING
RETURNED
```

Không lưu:

```text
OVERDUE
```

## Why?

Một Borrow được xem là quá hạn khi:

```text
status = BORROWING
AND
current_time > due_date
```

Ví dụ:

```text
borrowed_at = 2026-10-01
due_date    = 2026-10-15
status      = BORROWING
```

Nếu hiện tại:

```text
2026-10-20
```

thì:

```text
OVERDUE = true
```

Không cần thay đổi database.

### Advantage

Không cần một scheduled job chạy liên tục để:

```text
BORROWING → OVERDUE
```

Trạng thái quá hạn luôn được tính chính xác dựa trên thời điểm hiện tại.

---

# 12. Why `Return` Is Not a Table

Không tạo:

```text
returns
```

thành một table riêng.

Thay vào đó:

```text
borrows
 ├── borrowed_at
 ├── due_date
 ├── returned_at
 └── status
```

Khi User trả sách:

```text
returned_at = current_time
status = RETURNED
```

Lý do:

1. Một Borrow chỉ được trả một lần.
2. Return không có nghiệp vụ độc lập trong phiên bản hiện tại.
3. `returned_at` và `status` là một phần vòng đời của Borrow.
4. Giảm số lượng bảng.
5. Truy vấn lịch sử mượn/trả đơn giản hơn.

Trong tương lai có thể tạo `returns` riêng nếu cần:

```text
Return
 ├── returned_at
 ├── received_by
 ├── condition
 ├── fine
 └── note
```

---

# 13. Foreign Key Rules

## 13.1. `borrows.user_id`

```text
borrows.user_id
        ↓
users.id
```

Sử dụng:

```text
ON DELETE RESTRICT
```

Không cho xóa User nếu User vẫn có lịch sử Borrow.

---

## 13.2. `borrows.book_id`

```text
borrows.book_id
        ↓
books.id
```

Sử dụng:

```text
ON DELETE RESTRICT
```

Không cho xóa Book nếu Book còn lịch sử Borrow.

Điều này giúp bảo toàn lịch sử mượn sách.

---

## 13.3. `book_authors.book_id`

```text
book_authors.book_id
        ↓
books.id
```

Sử dụng:

```text
ON DELETE CASCADE
```

Xóa Book sẽ xóa các relationship của Book với Author.

---

## 13.4. `book_authors.author_id`

```text
book_authors.author_id
        ↓
authors.id
```

Sử dụng:

```text
ON DELETE RESTRICT
```

Không cho xóa Author nếu Author vẫn đang được sử dụng.

---

## 13.5. `book_categories.book_id`

```text
book_categories.book_id
        ↓
books.id
```

Sử dụng:

```text
ON DELETE CASCADE
```

---

## 13.6. `book_categories.category_id`

```text
book_categories.category_id
        ↓
categories.id
```

Sử dụng:

```text
ON DELETE RESTRICT
```

---

# 14. Index Design

Index được tạo cho các trường thường xuyên được:

* Search.
* Filter.
* Join.
* Sort.

## `users`

```text
username
email
status
```

Trong đó:

```text
username
email
```

đã được index thông qua `UNIQUE`.

Thêm:

```sql
CREATE INDEX idx_users_status
ON users(status);
```

---

## `books`

```text
isbn
title
```

`isbn` đã có index do `UNIQUE`.

Thêm:

```sql
CREATE INDEX idx_books_title
ON books(title);
```

---

## `authors`

```text
name
```

```sql
CREATE INDEX idx_authors_name
ON authors(name);
```

---

## `borrows`

Các query quan trọng:

```text
Lấy lịch sử mượn của User
Tìm các khoản đang mượn
Tìm Borrow theo Book
Tìm khoản gần/quá hạn
```

Index:

```sql
CREATE INDEX idx_borrows_user_status
ON borrows(user_id, status);

CREATE INDEX idx_borrows_book
ON borrows(book_id);

CREATE INDEX idx_borrows_due_date
ON borrows(due_date);
```

---

# 15. Complete Database Schema

Sơ đồ tổng thể:

```text
┌──────────────────┐
│      users       │
├──────────────────┤
│ PK id            │
│ username UNIQUE  │
│ email UNIQUE     │
│ password         │
│ full_name        │
│ phone            │
│ role             │
│ status           │
│ created_at       │
│ updated_at       │
└────────┬─────────┘
         │
         │ 1
         │
         │ N
┌────────▼─────────┐
│     borrows      │
├──────────────────┤
│ PK id            │
│ FK user_id       │
│ FK book_id       │
│ borrowed_at      │
│ due_date         │
│ returned_at      │
│ status           │
│ created_at       │
│ updated_at       │
└────────┬─────────┘
         │
         │ N
         │
         │ 1
┌────────▼─────────┐
│      books       │
├──────────────────┤
│ PK id            │
│ isbn UNIQUE      │
│ title            │
│ description      │
│ publisher        │
│ published_date   │
│ quantity         │
│ available_qty    │
│ cover_image      │
│ created_at       │
│ updated_at       │
└───────┬──────────┘
        │
        ├──────────────────────┐
        │                      │
        │ N                    │ N
        │                      │
        │ 1                    │ 1
┌───────▼─────────┐    ┌───────▼────────────┐
│  book_authors   │    │  book_categories   │
├─────────────────┤    ├────────────────────┤
│ PK book_id      │    │ PK book_id         │
│ PK author_id    │    │ PK category_id     │
└───────┬─────────┘    └─────────┬──────────┘
        │ N                      │ N
        │                        │
        │ 1                      │ 1
┌───────▼─────────┐    ┌─────────▼──────────┐
│     authors     │    │     categories     │
├─────────────────┤    ├────────────────────┤
│ PK id           │    │ PK id              │
│ name            │    │ name UNIQUE        │
│ biography       │    │ description        │
│ created_at      │    │ created_at         │
│ updated_at      │    │ updated_at         │
└─────────────────┘    │ updated_at         │
                       └────────────────────┘
```

---

# 16. Database Constraints Summary

| Table             | Constraint                       | Purpose                       |
| ----------------- | -------------------------------- | ----------------------------- |
| `users`           | PK `id`                          | Định danh User                |
| `users`           | UNIQUE `username`                | Không trùng username          |
| `users`           | UNIQUE `email`                   | Không trùng email             |
| `users`           | `status`                         | ACTIVE / LOCKED               |
| `books`           | PK `id`                          | Định danh Book                |
| `books`           | UNIQUE `isbn`                    | Không trùng ISBN              |
| `books`           | `quantity >= 0`                  | Không có số lượng âm          |
| `books`           | `available_quantity >= 0`        | Không có số lượng khả dụng âm |
| `books`           | `available_quantity <= quantity` | Đảm bảo bất biến số lượng     |
| `categories`      | UNIQUE `name`                    | Không trùng thể loại          |
| `book_authors`    | Composite PK                     | Chống relationship trùng      |
| `book_categories` | Composite PK                     | Chống relationship trùng      |
| `borrows`         | FK `user_id`                     | User phải tồn tại             |
| `borrows`         | FK `book_id`                     | Book phải tồn tại             |
| `borrows`         | `returned_at NULL`               | Cho phép khoản mượn chưa trả  |
| `borrows`         | `status`                         | BORROWING / RETURNED          |

---

# 17. Borrow Quantity Invariant

Đây là invariant quan trọng nhất của database:

```text
0 <= available_quantity <= quantity
```

Ví dụ hợp lệ:

```text
quantity = 10
available_quantity = 10

quantity = 10
available_quantity = 5

quantity = 10
available_quantity = 0
```

Ví dụ không hợp lệ:

```text
quantity = -1
available_quantity = 5
```

hoặc:

```text
quantity = 10
available_quantity = -1
```

hoặc:

```text
quantity = 10
available_quantity = 15
```

Application phải kiểm tra trước khi xử lý và database cũng phải có constraint bảo vệ.

---

# 18. Borrow Transaction Requirement

Thao tác mượn sách không chỉ là một câu SQL.

Nó gồm nhiều bước:

```text
BEGIN TRANSACTION
        │
        ▼
Lock Book
        │
        ▼
Check available_quantity
        │
        ▼
Check User
        │
        ▼
Check borrow rules
        │
        ▼
available_quantity = available_quantity - 1
        │
        ▼
INSERT Borrow
        │
        ▼
COMMIT
```

Nếu một bước thất bại:

```text
ROLLBACK
```

Toàn bộ thay đổi phải được hoàn tác.

Đây là cơ sở để triển khai:

```java
@Transactional
```

và cơ chế locking ở tầng Service/Repository.

---

# 19. Database Initialization Order

Khi tạo database từ đầu, thứ tự nên là:

```text
1. users
2. authors
3. categories
4. books
5. book_authors
6. book_categories
7. borrows
```

Lý do là bảng có Foreign Key phải được tạo sau bảng mà nó tham chiếu.

Ví dụ:

```text
books
  ↓
book_authors
```

phải tạo `books` trước `book_authors`.

Tương tự:

```text
users
  ↓
borrows

books
  ↓
borrows
```

---

# 20. Final Schema

Database cuối cùng:

```text
library_management
│
├── users
│
├── books
│
├── authors
│
├── categories
│
├── book_authors
│
├── book_categories
│
└── borrows
```

Relationship:

```text
User 1 ───────────── N Borrow N ───────────── 1 Book

Book N ───────────── N Author
 │
 └────────────────── N Category
```

Các nguyên tắc quan trọng cần giữ trong toàn bộ project:

```text
PK
FK
UNIQUE
NOT NULL
CHECK
INDEX
ON DELETE
TRANSACTION
CONCURRENCY
```

Database design này sẽ là cơ sở để triển khai tiếp:

```text
Database
   ↓
Entity
   ↓
Repository
   ↓
Service
   ↓
Transaction / Lock
   ↓
DTO
   ↓
Controller
   ↓
REST API
```
