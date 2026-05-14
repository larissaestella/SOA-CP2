package com.hotel.sishotel.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "guests")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Guest {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "full_name", length = 120, nullable = false)
    private String fullName;

    @Column(name = "document", length = 30, nullable = false, unique = true)
    private String document;

    @Column(name = "email", length = 120, nullable = false, unique = true)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
