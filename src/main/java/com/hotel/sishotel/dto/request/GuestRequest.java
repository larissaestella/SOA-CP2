package com.hotel.sishotel.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class GuestRequest {

    @NotBlank(message = "fullName is required")
    @Size(max = 120, message = "fullName must not exceed 120 characters")
    private String fullName;

    @NotBlank(message = "document is required")
    @Size(max = 30, message = "document must not exceed 30 characters")
    private String document;

    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid address")
    @Size(max = 120, message = "email must not exceed 120 characters")
    private String email;

    @Size(max = 30, message = "phone must not exceed 30 characters")
    private String phone;
}
