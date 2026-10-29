# Bài tập UDP Java

Dự án gồm 36 file Java, trong đó có 31 chương trình client chạy độc lập và
5 lớp dữ liệu dùng cho các bài truyền đối tượng.

## Mở bằng NetBeans

1. Tải ZIP từ GitHub và giải nén, hoặc clone repository.
2. Trong NetBeans, chọn **File → Open Project**.
3. Chọn đúng thư mục **chứa file `pom.xml`**, rồi bấm **Open Project**.
   Nếu giải nén ra `UDP-main/UDP-main`, hãy vào thư mục `UDP-main` bên trong.
4. Chờ NetBeans nạp project Maven và tải các plugin cần thiết lần đầu.
5. Mở **Source Packages → UDP**, chọn bài cần chạy, nhấn chuột phải
   **Run File** (Shift+F6).

Trước khi chạy, sửa `studentCode`, `qCode`, `host` và `port` trong bài tương ứng
theo đề của bạn. Mỗi bài có `main()` riêng; không cần chạy tất cả cùng lúc.
Các lớp `Book`, `Customer`, `Employee`, `Product`, `Student` là lớp dữ liệu,
không phải chương trình để chạy trực tiếp.

## Cấu trúc

```text
UDP/
├── pom.xml
├── README.md
└── src/main/java/UDP/
    ├── so.java
    ├── UDP2207_MaxMin.java
    ├── UDP2207_SoConThieu.java
    ├── UDP2208_ChuanHoa.java
    ├── UDP2208_KyTuNhieuNhat.java
    ├── UDP2209_Student.java
    ├── UDP2209_Customer.java
    └── ... các bài khác và lớp dữ liệu
```

## Biên dịch bằng dòng lệnh

Cần JDK và Maven. Có thể dùng JDK 21; mã nguồn được biên dịch cho Java 8.
Chạy tại thư mục chứa `pom.xml`:

```sh
mvn clean package
java -cp target/classes UDP.so
```

Thay `UDP.so` bằng tên lớp của bài muốn chạy, ví dụ `UDP.UDP2207_MaxMin`.
Lệnh chạy client sẽ gửi yêu cầu tới server được cấu hình trong bài.

## Nhóm bài

- **Số và mảng:** max/min, số còn thiếu, tổng chữ số, số nguyên tố,
  tổng/hiệu số nguyên lớn, cộng nhị phân, cửa sổ trượt.
- **Chuỗi:** chuẩn hóa, đếm/lọc ký tự, ký tự xuất hiện nhiều nhất,
  mã hóa Caesar, sắp xếp và khôi phục vị trí.
- **Đối tượng:** sinh viên, khách hàng, sản phẩm, sách, nhân viên.
