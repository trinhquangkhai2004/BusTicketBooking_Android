package com.busticket.backend.service.impl;

import com.busticket.backend.entity.Booking;
import com.busticket.backend.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingCleanupService {

    private final BookingRepository bookingRepository;

    // Chạy mỗi 1 phút một lần
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancelUnpaidBookings() {
        // Tìm các booking PENDING đã tạo trước đó 10 phút
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        
        List<Booking> expiredBookings = bookingRepository.findExpiredPendingBookings(tenMinutesAgo);
        
        if (!expiredBookings.isEmpty()) {
            for (Booking booking : expiredBookings) {
                booking.setStatus(Booking.BookingStatus.CANCELLED);
                log.info("Đã tự động huỷ Booking ID: {} do quá hạn 10 phút không thanh toán.", booking.getId());
            }
            bookingRepository.saveAll(expiredBookings);
        }
    }
}
