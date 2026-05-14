package com.hotel.sishotel.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationRequest {

    @NotBlank(message = "guestId is required")
    private String guestId;

    @NotBlank(message = "roomId is required")
    private String roomId;

    @NotNull(message = "checkinExpected is required (yyyy-MM-dd)")
    private LocalDate checkinExpected;

    @NotNull(message = "checkoutExpected is required (yyyy-MM-dd)")
    private LocalDate checkoutExpected;

    @Min(value = 1, message = "numGuests must be at least 1")
    private int numGuests = 1;
}
