CREATE DATABASE IF NOT EXISTS library_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_db;

CREATE TABLE users (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  username    VARCHAR(50)  NOT NULL,
  email       VARCHAR(100) NOT NULL,
  password    VARCHAR(255) NOT NULL,
  full_name   VARCHAR(100) NOT NULL,
  phone       VARCHAR(20)  NULL,
  role        VARCHAR(20)  NOT NULL,
  status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
  created_at  DATETIME     NOT NULL,
  updated_at  DATETIME     NOT NULL,
  CONSTRAINT uk_users_username UNIQUE (username),
  CONSTRAINT uk_users_email    UNIQUE (email),
  INDEX idx_users_status (status)
);

CREATE TABLE authors (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(150) NOT NULL,
  biography   TEXT         NULL,
  created_at  DATETIME     NOT NULL,
  updated_at  DATETIME     NOT NULL,
  INDEX idx_authors_name (name)
);

CREATE TABLE categories (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100) NOT NULL,
  description VARCHAR(255) NULL,
  created_at  DATETIME     NOT NULL,
  updated_at  DATETIME     NOT NULL,
  CONSTRAINT uk_categories_name UNIQUE (name)
);

CREATE TABLE books (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  isbn               VARCHAR(20)  NOT NULL,
  title              VARCHAR(255) NOT NULL,
  description        TEXT         NULL,
  publisher          VARCHAR(150) NULL,
  published_date     DATE         NULL,
  quantity           INT          NOT NULL,
  available_quantity INT          NOT NULL,
  cover_image        VARCHAR(500) NULL,
  created_at         DATETIME     NOT NULL,
  updated_at         DATETIME     NOT NULL,
  CONSTRAINT uk_books_isbn UNIQUE (isbn),
  CONSTRAINT ck_books_quantity  CHECK (quantity >= 0),
  CONSTRAINT ck_books_available CHECK (available_quantity >= 0
                                       AND available_quantity <= quantity),
  INDEX idx_books_title (title)
);

CREATE TABLE book_authors (
  book_id   BIGINT NOT NULL,
  author_id BIGINT NOT NULL,
  PRIMARY KEY (book_id, author_id),
  CONSTRAINT fk_ba_book   FOREIGN KEY (book_id)   REFERENCES books(id)   ON DELETE CASCADE,
--   Xóa sách thì xóa theo book_authors
  CONSTRAINT fk_ba_author FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE RESTRICT
--   Xóa tác giả nhưng tác giả đang có sách thì phải chặn
);

CREATE TABLE book_categories (
  book_id     BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  PRIMARY KEY (book_id, category_id),
  CONSTRAINT fk_bc_book     FOREIGN KEY (book_id)     REFERENCES books(id)      ON DELETE CASCADE,
  CONSTRAINT fk_bc_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
);

CREATE TABLE borrows (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT      NOT NULL,
  book_id     BIGINT      NOT NULL,
  borrowed_at DATETIME    NOT NULL,
  due_date    DATETIME    NOT NULL,
  returned_at DATETIME    NULL,
  status      VARCHAR(20) NOT NULL,
  created_at  DATETIME    NOT NULL,
  updated_at  DATETIME    NOT NULL,
  CONSTRAINT fk_borrows_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
  CONSTRAINT fk_borrows_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE RESTRICT,
  INDEX idx_borrows_user_status (user_id, status),
  INDEX idx_borrows_due_date (due_date)
);