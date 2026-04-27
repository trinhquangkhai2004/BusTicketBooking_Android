-- ============================================
-- Bus Ticket Booking - Database Initialization
-- ============================================

-- Đảm bảo database tồn tại với charset UTF-8
CREATE DATABASE IF NOT EXISTS `bus_ticket_booking`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `bus_ticket_booking`;

-- Grant full privileges cho app user
GRANT ALL PRIVILEGES ON `bus_ticket_booking`.* TO 'busticket'@'%';
FLUSH PRIVILEGES;

-- Log khởi tạo thành công
SELECT 'Database bus_ticket_booking initialized successfully!' AS status;
