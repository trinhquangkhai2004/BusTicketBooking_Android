package com.busticket.backend.repository;

import com.busticket.backend.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("SELECT t FROM Trip t WHERE t.route.departureLocation.id = :fromId " +
           "AND t.route.arrivalLocation.id = :toId " +
           "AND t.departureTime BETWEEN :startDate AND :endDate " +
           "AND t.status = 'SCHEDULED'")
    List<Trip> findAvailableTrips(
            @Param("fromId") Long fromId,
            @Param("toId") Long toId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
