# Trolyao New
Dự án Android Kotlin/Jetpack Compose tách riêng, không thay thế repo Trolyao cũ.

## Build
Dùng JDK 17, Android SDK 35 và GitHub Actions. Sau khi push lên nhánh `main`, mở Actions và tải artifact `Trolyao-New-debug`.

## Có trong bản nền
- Giao diện tối, bo góc, viền RGB.
- Trạng thái kết nối/cấp quyền Shizuku.
- Hướng dẫn quyền cửa sổ nổi.
- Menu nổi với đúng 3 mục: Macro Runner, Multiply Button Runner, Tab.
- Màn hình soạn danh sách bước Tap, Long press, Swipe, Delay.

## Giới hạn
Đây là nền tảng giao diện/build, chưa triển khai engine thực thi thao tác chạm tự động. Không coi nút macro là hoạt động trong game cho đến khi engine được bổ sung và kiểm thử trên thiết bị. Khoảng nghỉ 2 ms không bảo đảm 500 lần chạm/giây.
