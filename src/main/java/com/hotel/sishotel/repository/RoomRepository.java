package com.hotel.sishotel.repository;

import com.hotel.sishotel.domain.model.Room;
import com.hotel.sishotel.domain.model.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    Optional<Room> findByNumber(Integer number);
    boolean existsByNumber(Integer number);
    List<Room> findAllByStatus(RoomStatus status);
}
