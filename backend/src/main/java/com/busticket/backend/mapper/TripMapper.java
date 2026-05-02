package com.busticket.backend.mapper;

import com.busticket.backend.dto.TripDTO;
import com.busticket.backend.entity.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TripMapper {
    @Mapping(source = "route.departureLocation", target = "departureLocation")
    @Mapping(source = "route.arrivalLocation", target = "arrivalLocation")
    @Mapping(source = "route.duration", target = "duration")
    TripDTO toDto(Trip trip);
}
