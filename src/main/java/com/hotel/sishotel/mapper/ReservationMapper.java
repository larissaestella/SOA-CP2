package com.hotel.sishotel.mapper;

import com.hotel.sishotel.domain.model.Reservation;
import com.hotel.sishotel.dto.response.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationMapper {

    private final GuestMapper guestMapper;
    private final RoomMapper roomMapper;

    public ReservationResponse toResponse(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId())
                .guest(guestMapper.toResponse(r.getGuest()))
                .room(roomMapper.toResponse(r.getRoom()))
                .checkinExpected(r.getCheckinExpected())
                .checkoutExpected(r.getCheckoutExpected())
                .checkinAt(r.getCheckinAt())
                .checkoutAt(r.getCheckoutAt())
                .status(r.getStatus())
                .numGuests(r.getNumGuests())
                .estimatedAmount(r.getEstimatedAmount())
                .finalAmount(r.getFinalAmount())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
