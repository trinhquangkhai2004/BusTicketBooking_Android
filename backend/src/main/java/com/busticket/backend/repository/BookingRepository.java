package com.busticket.backend.repository;

import com.busticket.backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByTripId(Long tripId);

    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING' AND b.createdAt < :timeLimit")
    List<Booking> findExpiredPendingBookings(@Param("timeLimit") LocalDateTime timeLimit);
}
