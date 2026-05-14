package com.hotel.sishotel.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GuestResponse {
    private String id;
    private String fullName;
    private String document;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
}
