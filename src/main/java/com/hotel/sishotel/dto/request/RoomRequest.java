package com.hotel.sishotel.dto.request;

import com.hotel.sishotel.domain.model.RoomType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomRequest {

    @NotNull(message = "number is required")
    @Positive(message = "number must be a positive integer")
    private Integer number;

    @NotNull(message = "type is required (STANDARD | DELUXE | SUITE)")
    private RoomType type;

    @NotNull(message = "capacity is required")
    @Min(value = 1, message = "capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "pricePerNight is required")
    @DecimalMin(value = "0.01", message = "pricePerNight must be greater than zero")
    private BigDecimal pricePerNight;
}
