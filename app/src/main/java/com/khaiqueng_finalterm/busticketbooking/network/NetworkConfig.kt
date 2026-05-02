package com.khaiqueng_finalterm.busticketbooking.network

/**
 * Quản lý cấu hình mạng tập trung.
 * 
 * Hướng dẫn đổi IP:
 * - Nếu chạy máy ảo (Emulator): Dùng 10.0.2.2
 * - Nếu chạy máy thật cắm cáp/wifi: Đổi thành IP IPv4 của laptop (Ví dụ: 192.168.1.15)
 */
object NetworkConfig {
    // 1. Dùng cho Máy ảo Android Studio (Emulator)
    const val BASE_URL = "http://10.0.2.2:8088"
    
    // 2. Dùng cho Máy thật (Bỏ comment dòng dưới và comment dòng trên lại, thay bằng IP máy bạn)
    // const val BASE_URL = "http://192.168.x.x:8088"
}
