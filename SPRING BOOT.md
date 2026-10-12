# SPRING BOOT

1. Starter: Gói dependency theo chủ đề
2. Auto-configuration: Boot nhìn vào thư viên trong classpath rồi tự cấu hình
3. Embedded server: Tomcat nằm trong ứng dụng -> chỉ cần chạy main()

@SpringBootApplication: Annotation gồm 3 thứ

    - @Configuration: Lớp này có thể khai báo Bean

    - @EnableAutoConfiguration: Bật auto-configuration

    - @ComponentScan: Quét các class có @Component, @Service, @Repository, @Controller

II.

1.  Dependency: Nếu classA cần dùng classB để làm việc thì A phụ thuộc vào B

    VD: BookService cần BookRepository để đọc dữ liệu

2.  Ko có Spring: Tự tạo đối tượng

    public class BookService{

        private BookRepository bookRepository = new BookRepository(); // tự tạo

    }

    Vấn đề:
    - BookService bị dính chặt vào 1 lớp cụ thể, muốn thay = bản giả khi test ko đc

    - Nếu BookRepository cần thêm tham số thì mọi nơi new cần phải sửa

3.  IoC( Inversion of Control, đảo ngược quyền điều khiển)

    Thay vì class tự tạo và quản lý dependency của mình -> giao cho bên ngoài (Spring container).

    Class ở đây chỉ nói "Tôi cần cái này"

4.  DI( Dependency Injection: Tiêm phụ thuộc)

    Kỹ thuật thực hiện IoC: container tạo sẵn dependency -> đưa vào class qua constructor, setter or field

    IoC: Nguyên lý( Ai nắm quyền tạo Object?)

    DI: Cách làm ( Đưa dependency vào = cách nào?)

5.  Bean

    Bean là đối tượng đc Spring tạo ra và quản lý vòng đời

    Ko phải mọi Object trong chương trình là Bean: Chỉ có những Object do container quản lý mới là Bean

6.  ApplicationContext

    Là IoC Container:

        - Quét và đọc "bản vẽ"(class annotation, class cấu hình)

        - Tạo các Bean theo thứ tự phụ thuộc

        - Tiêm Bean vào nơi cần

        - Quản lý vòng đời: Tạo hủy

7.  Các Annotation đánh dấu Bean
    - @Component -> Class bất kì -> Bean dùng chung

    - @Service -> Tầng nghiệp vụ -> Thể hiện vai trò

    - @Repository -> Tầng dữ liệu -> Thêm việc chuyển lỗi DB sang exception của Spring

    - @RestController -> Tầng API -> Nhận Request, Trả Json

    - @Configuration + @Bean -> Class cấu hình -> Tự khai báo Bean thủ công, hữu ích cho class của thư viện ngoài

8.  Các cách tiêm dependency
    - @Autowired
      ...

    - private final ...

9.  Scope của Bean

    Singleton: 1 container chỉ có 1 instance cho mỗi bean -> Dùng chung cho mọi request

    Hệ quả: Ko lưu trạng thái riêng của request trong field của Service/Controller, vì nhiều thread dùng dung 1 đối tương

10. Tại sao cần dùng:
    - Về package Structure

      Tổ chức theo vai trò giúp biết việc này ở đâu

      Phân tầng: Mỗi tầng 1 cv, tầng trên gọi dưới

      Cho thêm đường dẫn để mở rộng:

    - Về IoC/DI:

      Toàn bộ Spring hoạt động dựa trên Bean, ko hiểu thì ko hiểu sao @Transactional đôi khi ko chạy hay vì sao NullPointerException xảy ra khi tự new 1 Service

      DI giúp giảm phụ thuộc cứng và để viết Test

11. 1 số Package

    config: Khai báo Bean cấu hình (Cors, PasswordEncoder)

    security: Jwt, filter, UserDetailService

    specification: Điều kiện tìm kiếm động

    util: các hàm tiện ích dùng chung

12. Vì sao tách service và service.impl

    Interface mô tả làm đc gì, còn class impl mô tả làm như thế nào

    Controller chỉ phụ thuộc vào interface -> dễ thay cài đặt, dễ giả lập khi test

III. Entity

1. Class Java ánh xạ một bảng

2. @MappedSupperClass

   Dùng cho class cha ko tự entity( ko có bảng riêng), nhưng các field của nó đc thừa kế vào các bản của các entity con.

3. JPA Auditing

   Cơ chế tự đồng điền các trường như thời gian tạo, sửa

4. Enum: ORDINAL hay STRING
   - ORDINAL lưu vị trí trong enum( 0, 1, 2: mặc định nếu ko khai báo)
   - STRING: Sẽ hiện thẳng tên, nếu thay đổi sẽ ko ảnh hưởng
5. Lombok cho entity
   - @Getter/@Setter: Sinh ra getter, setter
   - @NoArgsConstructor: Construcor rỗng(JPA bắt buộc có)
   - @Builder: Builder pattern: Giúp phép viết dễ đọc

       VD: Category.builder().name("Java").description("...").build();

   - Ko nên dùng @Data: sinh ra equals/ hashCode/toString trên mọi field, nên tự xử lý equals/hashCode khi cần
6. Các loại quan hệ
   - @ManyToOne -> N-1 -> Khóa ngoại nằm ở Phía @ManyToOne
     - VD: Borrow -> User, khóa ngoại user_id
   - @OneToMany -> 1-N -> Khóa ngoại nằm phía N
     - VD: User -> Borrow
   - @ManyToMany -> N-N -> Bảng trung gian
     - VD: Book <-> Author

7. @ManyToMany và @JoinTable
   - Database ko biểu diễn N-N bằng 1 cột, cần bảng trung gian chứa 2 khóa ngoại. @JoinTable mô tả bảng đó
   - joinColums = tôi || inverseJoinColumns =  phía bên kia

8. Owning side vad inverse side
    - Owning side: bên sỡ hữu, có @JoinTable. Thay đổi bên này sẽ khi xuống DB
    - Inverse side: Bên nghịch, khai báo mappedBy = "tên bảng bên kia". 

9. Một chiều hay 2 chiều
    - 2 chiều dễ bị gặp tình trạng lặp khi tải dữ liệu
    - 1 chiều: đơn giản, ko đồng bộ từ 2 phía

10. Vì sao dùng Set mà ko List
    - List: khi sửa danh sách thì xóa toàn bộ dòng trong bảng trung gian r chèn lại từ đầu
    - Set: chỉ xóa/ chèn đúng phần tử thay đổi, Set còn thể hiện 1 sách ko thể có 1 tác giả 2 lần

11. fetch: LAZY và EAGER
    - @ManytoMany: mặc định là LAZY, dữ liệu chỉ đc tải khi truy cập
    - optional: Quan hệ này có đc phép tồn tại ko( = true thì có thể chứ null, = false thì bắt buộc phải có )
    - nullable: cột db này đc chưa null ko
    - optional là mức ràng buộc ở JPA/Java, còn nullable là ở mức cột, nên khai báo cả 2 cho nhất quán
