package com.hotel.sishotel.service;

import com.hotel.sishotel.domain.exception.DuplicateResourceException;
import com.hotel.sishotel.domain.exception.InvalidReservationStateException;
import com.hotel.sishotel.domain.exception.ResourceNotFoundException;
import com.hotel.sishotel.domain.model.Room;
import com.hotel.sishotel.domain.model.RoomStatus;
import com.hotel.sishotel.dto.request.RoomRequest;
import com.hotel.sishotel.dto.response.RoomResponse;
import com.hotel.sishotel.mapper.RoomMapper;
import com.hotel.sishotel.repository.ReservationRepository;
import com.hotel.sishotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final RoomMapper roomMapper;

    @Transactional(readOnly = true)
    public List<RoomResponse> findAll() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> findActive() {
        return roomRepository.findAllByStatus(RoomStatus.ATIVO).stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoomResponse findById(String id) {
        return roomMapper.toResponse(getOrThrow(id));
    }

    @Transactional
    public RoomResponse create(RoomRequest req) {
        if (roomRepository.existsByNumber(req.getNumber())) {
            throw new DuplicateResourceException(
                    "Room with number " + req.getNumber() + " already exists.");
        }
        return roomMapper.toResponse(roomRepository.save(roomMapper.toEntity(req)));
    }

    @Transactional
    public RoomResponse update(String id, RoomRequest req) {
        Room room = getOrThrow(id);
        roomRepository.findByNumber(req.getNumber()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateResourceException(
                        "Room number " + req.getNumber() + " is already in use.");
            }
        });
        roomMapper.updateFromRequest(room, req);
        return roomMapper.toResponse(roomRepository.save(room));
    }

    @Transactional
    public void deactivate(String id) {
        Room room = getOrThrow(id);
        if (reservationRepository.hasActiveReservationsByRoomId(id)) {
            throw new InvalidReservationStateException(
                    "Room " + room.getNumber() + " has active reservations and cannot be deleted. " +
                    "It has been blocked from deletion. Cancel all reservations first.");
        }
        room.setStatus(RoomStatus.INATIVO);
        roomRepository.save(room);
    }

    @Transactional
    public RoomResponse activate(String id) {
        Room room = getOrThrow(id);
        room.setStatus(RoomStatus.ATIVO);
        return roomMapper.toResponse(roomRepository.save(room));
    }

    public Room getOrThrow(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with id: " + id));
    }
}
