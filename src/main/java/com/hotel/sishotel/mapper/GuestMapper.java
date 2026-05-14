package com.hotel.sishotel.mapper;

import com.hotel.sishotel.domain.model.Guest;
import com.hotel.sishotel.dto.request.GuestRequest;
import com.hotel.sishotel.dto.response.GuestResponse;
import org.springframework.stereotype.Component;

@Component
public class GuestMapper {

    public Guest toEntity(GuestRequest req) {
        return Guest.builder()
                .fullName(req.getFullName())
                .document(req.getDocument())
                .email(req.getEmail())
                .phone(req.getPhone())
                .build();
    }

    public GuestResponse toResponse(Guest g) {
        return GuestResponse.builder()
                .id(g.getId())
                .fullName(g.getFullName())
                .document(g.getDocument())
                .email(g.getEmail())
                .phone(g.getPhone())
                .createdAt(g.getCreatedAt())
                .build();
    }

    public void updateFromRequest(Guest guest, GuestRequest req) {
        guest.setFullName(req.getFullName());
        guest.setDocument(req.getDocument());
        guest.setEmail(req.getEmail());
        guest.setPhone(req.getPhone());
    }
}
