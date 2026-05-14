package com.hotel.sishotel.repository;

import com.hotel.sishotel.domain.model.Reservation;
import com.hotel.sishotel.domain.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    List<Reservation> findAllByGuestId(String guestId);

    List<Reservation> findAllByRoomId(String roomId);

    List<Reservation> findAllByStatus(ReservationStatus status);

    @Query("""
        SELECT r FROM Reservation r
        WHERE r.room.id = :roomId
          AND r.status <> com.hotel.sishotel.domain.model.ReservationStatus.CANCELED
          AND r.checkinExpected  < :checkoutExpected
          AND r.checkoutExpected > :checkinExpected
          AND (:excludeId IS NULL OR r.id <> :excludeId)
        """)
    List<Reservation> findOverlapping(
            @Param("roomId")           String    roomId,
            @Param("checkinExpected")  LocalDate checkinExpected,
            @Param("checkoutExpected") LocalDate checkoutExpected,
            @Param("excludeId")        String    excludeId
    );

    @Query("""
        SELECT COUNT(r) > 0 FROM Reservation r
        WHERE r.room.id = :roomId
          AND r.status <> com.hotel.sishotel.domain.model.ReservationStatus.CANCELED
        """)
    boolean hasActiveReservationsByRoomId(@Param("roomId") String roomId);
}
