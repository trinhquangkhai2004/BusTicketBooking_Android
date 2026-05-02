package com.busticket.backend.service.impl;

import com.busticket.backend.dto.BookingRequestDTO;
import com.busticket.backend.dto.BookingResponseDTO;
import com.busticket.backend.dto.SeatDTO;
import com.busticket.backend.entity.Booking;
import com.busticket.backend.entity.Seat;
import com.busticket.backend.entity.Trip;
import com.busticket.backend.entity.User;
import com.busticket.backend.exception.BusinessException;
import com.busticket.backend.exception.ResourceNotFoundException;
import com.busticket.backend.repository.BookingRepository;
import com.busticket.backend.repository.SeatRepository;
import com.busticket.backend.repository.TripRepository;
import com.busticket.backend.repository.UserRepository;
import com.busticket.backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<RLock> locks = new ArrayList<>();
        for (Long seatId : request.getSeatIds()) {
            RLock lock = redissonClient.getLock("lock:trip:" + trip.getId() + ":seat:" + seatId);
            locks.add(lock);
        }

        RLock multiLock = redissonClient.getMultiLock(locks.toArray(new RLock[0]));

        try {
            boolean isLocked = multiLock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new BusinessException("Ghế đang được người khác chọn. Vui lòng thử lại!");
            }

            List<Seat> selectedSeats = seatRepository.findAllById(request.getSeatIds());
            if (selectedSeats.size() != request.getSeatIds().size()) {
                throw new BusinessException("ID ghế không hợp lệ.");
            }

            List<Booking> existingBookings = bookingRepository.findByTripId(trip.getId());
            Set<Long> alreadyBookedSeatIds = existingBookings.stream()
                    .filter(b -> b.getStatus() != Booking.BookingStatus.CANCELLED)
                    .flatMap(b -> b.getSeats().stream())
                    .map(Seat::getId)
                    .collect(Collectors.toSet());

            for (Long seatId : request.getSeatIds()) {
                if (alreadyBookedSeatIds.contains(seatId)) {
                    throw new BusinessException("Ghế " + seatId + " đã được đặt.");
                }
            }

            BigDecimal totalAmount = trip.getPrice().multiply(new BigDecimal(selectedSeats.size()));

            Booking booking = Booking.builder()
                    .user(user)
                    .trip(trip)
                    .seats(Set.copyOf(selectedSeats))
                    .totalAmount(totalAmount)
                    .status(Booking.BookingStatus.PENDING)
                    .build();

            booking = bookingRepository.save(booking);

            BookingResponseDTO response = new BookingResponseDTO();
            response.setId(booking.getId());
            response.setTripRoute(trip.getRoute().getDepartureLocation().getName() + " -> " + trip.getRoute().getArrivalLocation().getName());
            response.setDepartureTime(trip.getDepartureTime());
            response.setTotalAmount(totalAmount);
            response.setStatus(booking.getStatus().name());

            return response;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("Đã xảy ra lỗi khi khoá ghế.");
        } finally {
            if (multiLock.isHeldByCurrentThread()) {
                multiLock.unlock();
            }
        }
    }

    @Override
    public List<SeatDTO> getSeatsForTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        List<Seat> allSeats = seatRepository.findByBusId(trip.getBus().getId());

        List<Booking> existingBookings = bookingRepository.findByTripId(trip.getId());
        Set<Long> bookedSeatIds = existingBookings.stream()
                .filter(b -> b.getStatus() != Booking.BookingStatus.CANCELLED)
                .flatMap(b -> b.getSeats().stream())
                .map(Seat::getId)
                .collect(Collectors.toSet());

        return allSeats.stream().map(seat -> {
            SeatDTO dto = new SeatDTO();
            dto.setId(seat.getId());
            dto.setSeatNumber(seat.getSeatNumber());
            dto.setIsBooked(bookedSeatIds.contains(seat.getId()));
            return dto;
        }).collect(Collectors.toList());
    }
}
