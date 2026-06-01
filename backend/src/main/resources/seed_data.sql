-- =============================================================
-- SEED DATA - BusTicketBooking
-- Thứ tự insert: users → locations → buses → seats → routes → trips → bookings
-- =============================================================

-- Xóa dữ liệu cũ (theo thứ tự ngược lại để tránh lỗi FK)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE booking_seats;
TRUNCATE TABLE bookings;
TRUNCATE TABLE trips;
TRUNCATE TABLE seats;
TRUNCATE TABLE routes;
TRUNCATE TABLE buses;
TRUNCATE TABLE locations;
-- KHÔNG xóa bảng users để giữ tài khoản đã login thật sự
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================
-- 1. LOCATIONS (Điểm đi / Điểm đến)
-- =============================================================
INSERT INTO locations (id, name, created_at, updated_at) VALUES
(1, 'Đà Nẵng',    NOW(), NOW()),
(2, 'Huế',        NOW(), NOW()),
(3, 'Hội An',     NOW(), NOW()),
(4, 'Quy Nhơn',   NOW(), NOW()),
(5, 'Quảng Ngãi', NOW(), NOW()),
(6, 'Tam Kỳ',     NOW(), NOW());

-- =============================================================
-- 2. BUSES (Xe)
-- =============================================================
INSERT INTO buses (id, license_plate, type, total_seats, status, created_at, updated_at) VALUES
(1, '43A-12345', 'SEAT',      48, 'ACTIVE',  NOW(), NOW()),  -- Xe ghế ngồi 48 chỗ
(2, '43B-67890', 'SLEEPER',   34, 'ACTIVE',  NOW(), NOW()),  -- Xe giường nằm 34 chỗ
(3, '43C-11111', 'LIMOUSINE', 16, 'ACTIVE',  NOW(), NOW()),  -- Xe Limousine 16 chỗ
(4, '75A-22222', 'SEAT',      48, 'ACTIVE',  NOW(), NOW()),
(5, '75B-33333', 'SLEEPER',   34, 'ACTIVE',  NOW(), NOW());

-- =============================================================
-- 3. SEATS (Ghế của từng xe)
-- =============================================================

-- Bus 1: 48 ghế SEAT (8 hàng x 6 cột - 43A-12345)
INSERT INTO seats (id, bus_id, seat_number, created_at, updated_at) VALUES
(1,  1, 'A1', NOW(), NOW()), (2,  1, 'B1', NOW(), NOW()), (3,  1, 'C1', NOW(), NOW()), (4,  1, 'D1', NOW(), NOW()), (5,  1, 'E1', NOW(), NOW()), (6,  1, 'F1', NOW(), NOW()),
(7,  1, 'A2', NOW(), NOW()), (8,  1, 'B2', NOW(), NOW()), (9,  1, 'C2', NOW(), NOW()), (10, 1, 'D2', NOW(), NOW()), (11, 1, 'E2', NOW(), NOW()), (12, 1, 'F2', NOW(), NOW()),
(13, 1, 'A3', NOW(), NOW()), (14, 1, 'B3', NOW(), NOW()), (15, 1, 'C3', NOW(), NOW()), (16, 1, 'D3', NOW(), NOW()), (17, 1, 'E3', NOW(), NOW()), (18, 1, 'F3', NOW(), NOW()),
(19, 1, 'A4', NOW(), NOW()), (20, 1, 'B4', NOW(), NOW()), (21, 1, 'C4', NOW(), NOW()), (22, 1, 'D4', NOW(), NOW()), (23, 1, 'E4', NOW(), NOW()), (24, 1, 'F4', NOW(), NOW()),
(25, 1, 'A5', NOW(), NOW()), (26, 1, 'B5', NOW(), NOW()), (27, 1, 'C5', NOW(), NOW()), (28, 1, 'D5', NOW(), NOW()), (29, 1, 'E5', NOW(), NOW()), (30, 1, 'F5', NOW(), NOW()),
(31, 1, 'A6', NOW(), NOW()), (32, 1, 'B6', NOW(), NOW()), (33, 1, 'C6', NOW(), NOW()), (34, 1, 'D6', NOW(), NOW()), (35, 1, 'E6', NOW(), NOW()), (36, 1, 'F6', NOW(), NOW()),
(37, 1, 'A7', NOW(), NOW()), (38, 1, 'B7', NOW(), NOW()), (39, 1, 'C7', NOW(), NOW()), (40, 1, 'D7', NOW(), NOW()), (41, 1, 'E7', NOW(), NOW()), (42, 1, 'F7', NOW(), NOW()),
(43, 1, 'A8', NOW(), NOW()), (44, 1, 'B8', NOW(), NOW()), (45, 1, 'C8', NOW(), NOW()), (46, 1, 'D8', NOW(), NOW()), (47, 1, 'E8', NOW(), NOW()), (48, 1, 'F8', NOW(), NOW());

-- Bus 2: 34 ghế SLEEPER (Xe giường nằm - 43B-67890)
INSERT INTO seats (id, bus_id, seat_number, created_at, updated_at) VALUES
(49, 2, 'L-A1', NOW(), NOW()), (50, 2, 'L-A2', NOW(), NOW()),
(51, 2, 'L-B1', NOW(), NOW()), (52, 2, 'L-B2', NOW(), NOW()),
(53, 2, 'L-C1', NOW(), NOW()), (54, 2, 'L-C2', NOW(), NOW()),
(55, 2, 'L-D1', NOW(), NOW()), (56, 2, 'L-D2', NOW(), NOW()),
(57, 2, 'L-E1', NOW(), NOW()), (58, 2, 'L-E2', NOW(), NOW()),
(59, 2, 'L-F1', NOW(), NOW()), (60, 2, 'L-F2', NOW(), NOW()),
(61, 2, 'L-G1', NOW(), NOW()), (62, 2, 'L-G2', NOW(), NOW()),
(63, 2, 'L-H1', NOW(), NOW()), (64, 2, 'L-H2', NOW(), NOW()),
(65, 2, 'U-A1', NOW(), NOW()), (66, 2, 'U-A2', NOW(), NOW()),
(67, 2, 'U-B1', NOW(), NOW()), (68, 2, 'U-B2', NOW(), NOW()),
(69, 2, 'U-C1', NOW(), NOW()), (70, 2, 'U-C2', NOW(), NOW()),
(71, 2, 'U-D1', NOW(), NOW()), (72, 2, 'U-D2', NOW(), NOW()),
(73, 2, 'U-E1', NOW(), NOW()), (74, 2, 'U-E2', NOW(), NOW()),
(75, 2, 'U-F1', NOW(), NOW()), (76, 2, 'U-F2', NOW(), NOW()),
(77, 2, 'U-G1', NOW(), NOW()), (78, 2, 'U-G2', NOW(), NOW()),
(79, 2, 'U-H1', NOW(), NOW()), (80, 2, 'U-H2', NOW(), NOW()),
(81, 2, 'U-I1', NOW(), NOW()), (82, 2, 'U-I2', NOW(), NOW());

-- Bus 3: 16 ghế LIMOUSINE (43C-11111)
INSERT INTO seats (id, bus_id, seat_number, created_at, updated_at) VALUES
(83,  3, 'VIP-1',  NOW(), NOW()), (84,  3, 'VIP-2',  NOW(), NOW()),
(85,  3, 'VIP-3',  NOW(), NOW()), (86,  3, 'VIP-4',  NOW(), NOW()),
(87,  3, 'VIP-5',  NOW(), NOW()), (88,  3, 'VIP-6',  NOW(), NOW()),
(89,  3, 'VIP-7',  NOW(), NOW()), (90,  3, 'VIP-8',  NOW(), NOW()),
(91,  3, 'VIP-9',  NOW(), NOW()), (92,  3, 'VIP-10', NOW(), NOW()),
(93,  3, 'VIP-11', NOW(), NOW()), (94,  3, 'VIP-12', NOW(), NOW()),
(95,  3, 'VIP-13', NOW(), NOW()), (96,  3, 'VIP-14', NOW(), NOW()),
(97,  3, 'VIP-15', NOW(), NOW()), (98,  3, 'VIP-16', NOW(), NOW());

-- =============================================================
-- 4. ROUTES (Tuyến đường)
-- =============================================================
-- departure_location_id → arrival_location_id
INSERT INTO routes (id, departure_location_id, arrival_location_id, distance, duration, created_at, updated_at) VALUES
(1, 1, 2, 100.0, '2h 30m', NOW(), NOW()),   -- Đà Nẵng → Huế
(2, 2, 1, 100.0, '2h 30m', NOW(), NOW()),   -- Huế → Đà Nẵng
(3, 1, 3, 30.0,  '45m',    NOW(), NOW()),   -- Đà Nẵng → Hội An
(4, 3, 1, 30.0,  '45m',    NOW(), NOW()),   -- Hội An → Đà Nẵng
(5, 1, 4, 300.0, '6h 00m', NOW(), NOW()),   -- Đà Nẵng → Quy Nhơn
(6, 4, 1, 300.0, '6h 00m', NOW(), NOW()),   -- Quy Nhơn → Đà Nẵng
(7, 2, 4, 400.0, '8h 00m', NOW(), NOW()),   -- Huế → Quy Nhơn
(8, 3, 2, 130.0, '3h 15m', NOW(), NOW());   -- Hội An → Huế

-- =============================================================
-- 5. TRIPS (Chuyến xe - nhiều chuyến để test)
-- =============================================================
-- Chú ý: price là BigDecimal trong Java → DECIMAL trong MySQL
INSERT INTO trips (id, route_id, bus_id, departure_time, arrival_time, price, status, created_at, updated_at) VALUES

-- Tuyến Đà Nẵng → Huế (route_id=1), nhiều ngày để test
(1,  1, 1, '2026-05-11 06:00:00', '2026-05-11 08:30:00', 120000, 'SCHEDULED', NOW(), NOW()),
(2,  1, 2, '2026-05-11 08:00:00', '2026-05-11 10:30:00', 180000, 'SCHEDULED', NOW(), NOW()),
(3,  1, 3, '2026-05-11 12:00:00', '2026-05-11 14:30:00', 350000, 'SCHEDULED', NOW(), NOW()),
(4,  1, 1, '2026-05-11 15:00:00', '2026-05-11 17:30:00', 120000, 'SCHEDULED', NOW(), NOW()),
(5,  1, 2, '2026-05-12 06:00:00', '2026-05-12 08:30:00', 180000, 'SCHEDULED', NOW(), NOW()),
(6,  1, 1, '2026-05-12 14:00:00', '2026-05-12 16:30:00', 120000, 'SCHEDULED', NOW(), NOW()),

-- Tuyến Huế → Đà Nẵng (route_id=2)
(7,  2, 4, '2026-05-11 07:00:00', '2026-05-11 09:30:00', 120000, 'SCHEDULED', NOW(), NOW()),
(8,  2, 5, '2026-05-11 13:00:00', '2026-05-11 15:30:00', 180000, 'SCHEDULED', NOW(), NOW()),
(9,  2, 4, '2026-05-12 07:00:00', '2026-05-12 09:30:00', 120000, 'SCHEDULED', NOW(), NOW()),

-- Tuyến Đà Nẵng → Hội An (route_id=3)
(10, 3, 1, '2026-05-11 07:30:00', '2026-05-11 08:15:00',  70000, 'SCHEDULED', NOW(), NOW()),
(11, 3, 3, '2026-05-11 10:30:00', '2026-05-11 11:15:00', 150000, 'SCHEDULED', NOW(), NOW()),
(12, 3, 1, '2026-05-12 07:30:00', '2026-05-12 08:15:00',  70000, 'SCHEDULED', NOW(), NOW()),

-- Tuyến Đà Nẵng → Quy Nhơn (route_id=5)
(13, 5, 2, '2026-05-11 05:00:00', '2026-05-11 11:00:00', 270000, 'SCHEDULED', NOW(), NOW()),
(14, 5, 5, '2026-05-11 20:00:00', '2026-05-12 02:00:00', 300000, 'SCHEDULED', NOW(), NOW()),
(15, 5, 2, '2026-05-12 05:00:00', '2026-05-12 11:00:00', 270000, 'SCHEDULED', NOW(), NOW()),

-- Tuyến Hội An → Huế (route_id=8)
(16, 8, 1, '2026-05-11 09:00:00', '2026-05-11 12:15:00', 150000, 'SCHEDULED', NOW(), NOW()),
(17, 8, 3, '2026-05-12 09:00:00', '2026-05-12 12:15:00', 350000, 'SCHEDULED', NOW(), NOW()),

-- Một chuyến đã COMPLETED để test màn hình "Vé của tôi"
(18, 1, 1, '2026-05-09 06:00:00', '2026-05-09 08:30:00', 120000, 'COMPLETED', NOW(), NOW()),
(19, 5, 2, '2026-05-08 05:00:00', '2026-05-08 11:00:00', 270000, 'COMPLETED', NOW(), NOW());

-- =============================================================
-- 6. BOOKINGS (Đặt vé mẫu)
-- LƯU Ý: Thay <YOUR_USER_ID> bằng ID thực của user bạn đăng nhập.
--        Để lấy ID: SELECT id FROM users WHERE email = 'your-email@gmail.com';
-- =============================================================

-- Booking 1: Đặt chuyến COMPLETED (để test tab Lịch sử vé)
-- INSERT INTO bookings (id, user_id, trip_id, total_amount, status, created_at, updated_at) VALUES
-- (1, <YOUR_USER_ID>, 18, 240000, 'CONFIRMED', NOW(), NOW());

-- Booking 2: Đặt chuyến sắp tới (để test tab Vé của tôi)
-- INSERT INTO bookings (id, user_id, trip_id, total_amount, status, created_at, updated_at) VALUES
-- (2, <YOUR_USER_ID>, 1, 120000, 'CONFIRMED', NOW(), NOW());

-- INSERT INTO booking_seats (booking_id, seat_id) VALUES (1, 1), (1, 2);
-- INSERT INTO booking_seats (booking_id, seat_id) VALUES (2, 3);

-- =============================================================
-- KIỂM TRA DỮ LIỆU
-- =============================================================
-- SELECT * FROM locations;
-- SELECT * FROM buses;
-- SELECT COUNT(*) FROM seats;     -- Phải là 98
-- SELECT * FROM routes;
-- SELECT * FROM trips ORDER BY departure_time;
-- SELECT u.email, b.status, t.departure_time, t.price
--   FROM bookings b
--   JOIN users u ON b.user_id = u.id
--   JOIN trips t ON b.trip_id = t.id;
