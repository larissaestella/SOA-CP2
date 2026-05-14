package com.hotel.sishotel.repository;

import com.hotel.sishotel.domain.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, String> {
    Optional<Guest> findByDocument(String document);
    Optional<Guest> findByEmail(String email);
    boolean existsByDocument(String document);
    boolean existsByEmail(String email);
}
