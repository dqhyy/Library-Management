# Requirement Analysis

## 1. System Overview

### 1.1. System Description

Library Management System là hệ thống quản lý thư viện cho phép người đọc đăng ký tài khoản, tìm kiếm sách, mượn sách và trả sách trong thời hạn quy định.

Thủ thư (Admin) có quyền quản lý sách, tác giả, thể loại, người dùng và theo dõi toàn bộ hoạt động mượn/trả trong hệ thống.

Hệ thống tập trung giải quyết ba vấn đề chính:

1. Quản lý danh mục sách gồm sách, tác giả và thể loại.
2. Quản lý vòng đời của một lần mượn: mượn → đang mượn → trả / quá hạn.
3. Đảm bảo số lượng sách luôn chính xác khi có nhiều người dùng cùng thực hiện thao tác mượn/trả.

Đặc biệt, thao tác mượn sách phải đảm bảo tính nhất quán dữ liệu khi xảy ra nhiều request đồng thời.

---

# 2. Actors

Hệ thống có ba loại actor:

| Actor                | Description               | Permissions                             |
| -------------------- | ------------------------- | --------------------------------------- |
| Guest                | Người dùng chưa đăng nhập | Xem/tìm sách, đăng ký, đăng nhập        |
| User (`ROLE_USER`)   | Độc giả đã đăng nhập      | Mượn/trả sách, xem lịch sử của bản thân |
| Admin (`ROLE_ADMIN`) | Thủ thư/quản trị viên     | Quản lý toàn bộ hệ thống                |

> **Note:** `Guest` không phải là một role được lưu trong database. Guest đại diện cho người dùng chưa được xác thực.

---

# 3. Functional Requirements

## 3.1. Authentication & Account Management

### Guest

Guest có thể:

* Xem danh sách sách.
* Tìm kiếm sách.
* Xem thông tin sách.
* Đăng ký tài khoản.
* Đăng nhập.

### User

User có thể:

* Đăng nhập.
* Đăng xuất.
* Xem thông tin cá nhân.
* Thực hiện các thao tác mượn/trả sách.
* Xem lịch sử mượn sách của bản thân.

### Admin

Admin có thể:

* Đăng nhập.
* Quản lý người dùng.
* Khóa/mở khóa tài khoản.
* Xem thông tin người dùng.

---

# 4. Book Management Requirements

## 4.1. User Requirements

User có thể:

* Tìm kiếm sách.
* Lọc sách.
* Xem danh sách sách.
* Xem chi tiết sách.
* Xem tình trạng còn/hết của sách.

## 4.2. Admin Requirements

Admin có thể:

* Thêm sách.
* Sửa sách.
* Xóa sách.
* Xem danh sách sách.
* Tìm kiếm sách.
* Lọc sách.
* Phân trang.
* Xem số lượng sách.
* Xem số lượng sách còn có thể cho mượn.

---

# 5. Author Management Requirements

Admin có thể:

* Thêm tác giả.
* Xem danh sách tác giả.
* Xem chi tiết tác giả.
* Sửa tác giả.
* Xóa tác giả.
* Tìm kiếm tác giả theo tên.
* Phân trang danh sách tác giả.

---

# 6. Category Management Requirements

Admin có thể:

* Thêm thể loại.
* Xem danh sách thể loại.
* Xem chi tiết thể loại.
* Sửa thể loại.
* Xóa thể loại.
* Tìm kiếm thể loại theo tên.
* Phân trang danh sách thể loại.

---

# 7. User Management Requirements

Admin có thể:

* Xem danh sách người dùng.
* Xem chi tiết người dùng.
* Sửa thông tin người dùng.
* Khóa tài khoản.
* Mở khóa tài khoản.
* Xóa người dùng.
* Tìm kiếm người dùng.
* Lọc người dùng theo trạng thái.
* Phân trang danh sách người dùng.

User có hai trạng thái chính:

```text
ACTIVE
LOCKED
```

---

# 8. Borrow & Return Requirements

## 8.1. Borrow Book

User có thể mượn sách nếu thỏa mãn tất cả điều kiện:

* Sách tồn tại.
* Sách còn bản có thể cho mượn.
* Tài khoản User đang hoạt động.
* User chưa đạt giới hạn số sách được mượn.
* User không có sách quá hạn.
* User chưa mượn chính cuốn sách đó mà chưa trả.

Khi mượn thành công:

```text
availableQuantity = availableQuantity - 1
```

Đồng thời tạo một `Borrow` mới:

```text
status = BORROWING
borrowedAt = current time
dueDate = borrowedAt + X days
```

Thời hạn mặc định có thể là:

```text
14 days
```

---

# 9. Return Book Requirements

User có thể trả sách đang mượn.

Khi trả thành công:

```text
returnedAt = current time
status = RETURNED
availableQuantity = availableQuantity + 1
```

Nếu:

```text
current time > dueDate
```

thì khoản mượn được xác định là quá hạn.

---

# 10. Borrow History Requirements

## User

User có thể xem:

* Sách đang mượn.
* Hạn trả.
* Trạng thái khoản mượn.
* Lịch sử mượn/trả.

Có thể lọc theo:

```text
BORROWING
RETURNED
OVERDUE
ALL
```

## Admin

Admin có thể:

* Xem toàn bộ khoản mượn.
* Xem lịch sử mượn/trả.
* Lọc khoản mượn theo trạng thái.

---

# 11. Dashboard & Statistics Requirements

Admin có thể xem các thống kê cơ bản:

* Tổng số sách.
* Tổng số người dùng.
* Tổng số lượt đang mượn.
* Tổng số lượt quá hạn.
* Số lượng sách còn lại.
* Các thống kê liên quan đến hoạt động mượn/trả.

---

# 12. Business Requirements

## BR-01: Book Availability

Một sách chỉ được phép mượn khi:

```text
availableQuantity > 0
```

Nếu:

```text
availableQuantity = 0
```

hệ thống phải từ chối yêu cầu mượn.

---

## BR-02: Locked User

User có trạng thái:

```text
LOCKED
```

không được phép mượn sách.

---

## BR-03: Maximum Borrow Limit

Một User chỉ được mượn tối đa một số lượng sách nhất định.

Ví dụ:

```text
MAX_BORROW = 5
```

Nếu User đã đạt giới hạn, hệ thống phải từ chối yêu cầu mượn tiếp.

---

## BR-04: Overdue Restriction

Nếu User đang có ít nhất một khoản mượn quá hạn, User phải trả các sách quá hạn trước khi được phép mượn sách mới.

---

## BR-05: Duplicate Borrow

User không được mượn cùng một đầu sách nhiều lần khi khoản mượn trước đó chưa được trả.

---

## BR-06: Borrow Quantity Consistency

Khi mượn:

```text
availableQuantity = availableQuantity - 1
```

Khi trả:

```text
availableQuantity = availableQuantity + 1
```

Hệ thống phải đảm bảo bất biến:

```text
0 <= availableQuantity <= quantity
```

Bất biến này phải luôn được duy trì.

---

# 13. Borrow Lifecycle

Một khoản mượn có vòng đời:

```text
BORROWING
    │
    │ return
    ▼
RETURNED
```

Trường hợp quá hạn được xác định dựa trên:

```text
currentTime > dueDate
```

Việc `OVERDUE` được lưu trực tiếp trong database hay được tính toán từ `dueDate` sẽ được quyết định trong giai đoạn thiết kế entity.

---

# 14. Data Requirements

## 14.1. User

User lưu thông tin:

```text
id
username
email
password
fullName
role
status
createdAt
updatedAt
```

Trong đó:

```text
role = ROLE_USER | ROLE_ADMIN

status = ACTIVE | LOCKED
```

---

## 14.2. Book

Book đại diện cho một đầu sách:

```text
id
isbn
title
description
publisher
publishedDate
quantity
availableQuantity
coverImage
createdAt
updatedAt
```

Business constraint:

```text
0 <= availableQuantity <= quantity
```

---

## 14.3. Author

```text
id
name
createdAt
updatedAt
```

---

## 14.4. Category

```text
id
name
createdAt
updatedAt
```

`name` phải là duy nhất.

---

## 14.5. Borrow

Borrow đại diện cho **một lần mượn của một User đối với một Book**:

```text
id
userId
bookId
borrowedAt
dueDate
returnedAt
status
createdAt
updatedAt
```

Status:

```text
BORROWING
RETURNED
```

---

# 15. Entity Relationships

Các quan hệ chính:

```text
User 1 ───── N Borrow

Book 1 ───── N Borrow

Book N ───── N Author

Book N ───── N Category
```

Chi tiết:

### User – Borrow

Một User có thể có nhiều lần mượn:

```text
User 1 ───── N Borrow
```

`Borrow` chứa:

```text
user_id
```

---

### Book – Borrow

Một Book có thể được mượn nhiều lần theo thời gian:

```text
Book 1 ───── N Borrow
```

`Borrow` chứa:

```text
book_id
```

---

### Book – Author

Một Book có thể có nhiều Author và một Author có thể viết nhiều Book:

```text
Book N ───── N Author
```

Database sử dụng bảng trung gian:

```text
book_authors
```

---

### Book – Category

Một Book có thể thuộc nhiều Category và một Category có thể chứa nhiều Book:

```text
Book N ───── N Category
```

Database sử dụng bảng trung gian:

```text
book_categories
```

---

# 16. Return Entity Decision

Hệ thống **không tạo `Return` entity riêng**.

Thay vào đó, thông tin trả sách được lưu trực tiếp trong `Borrow`:

```text
returnedAt
status
```

Lý do:

1. Một `Borrow` tương ứng với một lần mượn.
2. Một lần mượn chỉ được trả một lần.
3. Trả sách là một sự kiện trong vòng đời của `Borrow`.
4. Không cần tạo thêm entity và quan hệ `Borrow - Return`.
5. Việc truy vấn lịch sử mượn/trả đơn giản hơn.

Do đó:

```text
Borrow
 ├── borrowedAt
 ├── dueDate
 ├── returnedAt
 └── status
```

Nếu trong tương lai hệ thống cần quản lý:

* Phiếu trả.
* Tiền phạt.
* Người tiếp nhận sách.
* Ghi chú tình trạng sách.
* Các nghiệp vụ phát sinh khi trả.

thì có thể tách `Return` thành entity riêng.

---

# 17. Database Requirements

Database dự kiến gồm các bảng:

```text
users
books
authors
categories
book_authors
book_categories
borrows
```

## users

```text
id PK
username UNIQUE
email UNIQUE
password
full_name
role
status
created_at
updated_at
```

## books

```text
id PK
isbn UNIQUE
title
description
publisher
published_date
quantity
available_quantity
cover_image
created_at
updated_at
```

## authors

```text
id PK
name
created_at
updated_at
```

## categories

```text
id PK
name UNIQUE
created_at
updated_at
```

## book_authors

```text
book_id FK
author_id FK

PRIMARY KEY (book_id, author_id)
```

## book_categories

```text
book_id FK
category_id FK

PRIMARY KEY (book_id, category_id)
```

## borrows

```text
id PK
user_id FK
book_id FK
borrowed_at
due_date
returned_at
status
created_at
updated_at
```

---

# 18. Non-Functional Requirements

## NFR-01: Data Consistency

Hệ thống phải đảm bảo dữ liệu số lượng sách luôn chính xác:

```text
0 <= availableQuantity <= quantity
```

---

## NFR-02: Concurrency Safety

Khi nhiều User cùng mượn một cuốn sách tại cùng thời điểm, hệ thống không được để xảy ra tình trạng:

```text
availableQuantity < 0
```

hoặc cho phép nhiều User mượn vượt quá số lượng sách thực tế.

Thao tác mượn/trả phải được xử lý bằng transaction và cơ chế locking phù hợp.

---

## NFR-03: Authentication & Authorization

Hệ thống phải kiểm soát quyền truy cập dựa trên role:

```text
ROLE_USER
ROLE_ADMIN
```

Các API quản trị chỉ được phép truy cập bởi Admin.

Các API mượn/trả yêu cầu User đã đăng nhập.

Các API công khai có thể được truy cập bởi Guest.

---

## NFR-04: Validation

Dữ liệu đầu vào phải được kiểm tra trước khi xử lý.

Ví dụ:

* Username không được trùng.
* Email không được trùng.
* ISBN không được trùng.
* Quantity không được âm.
* Available quantity không được âm.
* Due date phải hợp lệ.

---

## NFR-05: Error Handling

Hệ thống phải trả về lỗi rõ ràng khi nghiệp vụ không hợp lệ.

Ví dụ:

```text
Book not found
Book is unavailable
User is locked
Borrow limit exceeded
User has overdue books
Book already borrowed
```

---

# 19. System Scope

## In Scope

Phiên bản hiện tại bao gồm:

* Authentication.
* User management.
* Book management.
* Author management.
* Category management.
* Borrow management.
* Return management.
* Borrow history.
* Search.
* Filter.
* Pagination.
* Dashboard statistics.
* Role-based authorization.
* Transaction.
* Concurrency handling.

## Out of Scope

Chưa bao gồm:

* Thanh toán tiền phạt online.
* Đặt trước sách.
* Thông báo email.
* Thông báo push notification.
* Quản lý nhiều chi nhánh thư viện.
* Quản lý vị trí/kệ sách.
* Quản lý phiếu trả riêng.
* Recommendation system.

Các chức năng này có thể được mở rộng trong tương lai.

---

# 20. Requirement Summary

Luồng nghiệp vụ chính:

```text
                    ┌──────────────┐
                    │    Guest     │
                    └──────┬───────┘
                           │
                    Register / Login
                           │
                           ▼
                    ┌──────────────┐
                    │     User     │
                    └──────┬───────┘
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
          Search Book   Borrow Book   History
                           │
                           ▼
                      BORROWING
                           │
                         Return
                           │
                           ▼
                       RETURNED


                    ┌──────────────┐
                    │    Admin     │
                    └──────┬───────┘
                           │
          ┌────────────────┼────────────────┐
          ▼                ▼                ▼
       Books            Users          Borrow Records
          │
     ┌────┴────┐
     ▼         ▼
 Authors    Categories
```

### Core business flow

```text
User requests borrow
        │
        ▼
Check Book exists
        │
        ▼
Check availableQuantity > 0
        │
        ▼
Check User ACTIVE
        │
        ▼
Check borrow limit
        │
        ▼
Check overdue books
        │
        ▼
Check duplicate borrow
        │
        ▼
Decrease availableQuantity
        │
        ▼
Create Borrow
        │
        ▼
Borrow successful
```

### Return flow

```text
User requests return
        │
        ▼
Find active Borrow
        │
        ▼
Set returnedAt = now
        │
        ▼
Set status = RETURNED
        │
        ▼
Increase availableQuantity
        │
        ▼
Return successful
```

Đây là baseline requirement cho toàn bộ quá trình thiết kế **Entity → Repository → Service → Controller → Security → Transaction/Concurrency → API → Frontend** của Library Management System.
