package com.hotel.sishotel.dto.response;

import com.hotel.sishotel.domain.model.RoomStatus;
import com.hotel.sishotel.domain.model.RoomType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RoomResponse {
    private String id;
    private Integer number;
    private RoomType type;
    private Integer capacity;
    private BigDecimal pricePerNight;
    private RoomStatus status;
}
