package com.hotel.sishotel.dto.response;

import com.hotel.sishotel.domain.model.ReservationStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ReservationResponse {
    private String id;
    private GuestResponse guest;
    private RoomResponse room;
    private LocalDate checkinExpected;
    private LocalDate checkoutExpected;
    private LocalDateTime checkinAt;
    private LocalDateTime checkoutAt;
    private ReservationStatus status;
    private Integer numGuests;
    private BigDecimal estimatedAmount;
    private BigDecimal finalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
