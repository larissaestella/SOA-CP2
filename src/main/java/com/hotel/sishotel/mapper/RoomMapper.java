package com.hotel.sishotel.mapper;

import com.hotel.sishotel.domain.model.Room;
import com.hotel.sishotel.domain.model.RoomStatus;
import com.hotel.sishotel.dto.request.RoomRequest;
import com.hotel.sishotel.dto.response.RoomResponse;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public Room toEntity(RoomRequest req) {
        return Room.builder()
                .number(req.getNumber())
                .type(req.getType())
                .capacity(req.getCapacity())
                .pricePerNight(req.getPricePerNight())
                .status(RoomStatus.ATIVO)
                .build();
    }

    public RoomResponse toResponse(Room r) {
        return RoomResponse.builder()
                .id(r.getId())
                .number(r.getNumber())
                .type(r.getType())
                .capacity(r.getCapacity())
                .pricePerNight(r.getPricePerNight())
                .status(r.getStatus())
                .build();
    }

    public void updateFromRequest(Room room, RoomRequest req) {
        room.setNumber(req.getNumber());
        room.setType(req.getType());
        room.setCapacity(req.getCapacity());
        room.setPricePerNight(req.getPricePerNight());
    }
}
