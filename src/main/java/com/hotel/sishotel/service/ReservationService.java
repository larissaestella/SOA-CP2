package com.hotel.sishotel.service;

import com.hotel.sishotel.domain.exception.*;
import com.hotel.sishotel.domain.model.*;
import com.hotel.sishotel.dto.request.ReservationRequest;
import com.hotel.sishotel.dto.response.ReservationResponse;
import com.hotel.sishotel.mapper.ReservationMapper;
import com.hotel.sishotel.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final GuestService guestService;
    private final RoomService roomService;
    private final ReservationMapper mapper;

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ReservationResponse findById(String id) {
        return mapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findByGuest(String guestId) {
        guestService.getOrThrow(guestId);
        return reservationRepository.findAllByGuestId(guestId).stream()
                .map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findByRoom(String roomId) {
        roomService.getOrThrow(roomId);
        return reservationRepository.findAllByRoomId(roomId).stream()
                .map(mapper::toResponse).toList();
    }

    // create reservation

    @Transactional
    public ReservationResponse create(ReservationRequest req) {
        // Rule 1 – dates
        validateDateRange(req.getCheckinExpected(), req.getCheckoutExpected());

        Guest guest = guestService.getOrThrow(req.getGuestId());
        Room  room  = roomService.getOrThrow(req.getRoomId());

        // Room must be active
        if (room.getStatus() == RoomStatus.INATIVO) {
            throw new RoomInactiveException(
                    "Room " + room.getNumber() + " is INATIVO and cannot be reserved.");
        }

        // Rule 3 – capacity
        validateCapacity(req.getNumGuests(), room.getCapacity(), room.getNumber());

        // Rule 2 – availability
        validateAvailability(room.getId(), req.getCheckinExpected(), req.getCheckoutExpected(), null);

        // Rule 6 – estimated amount
        long days = ChronoUnit.DAYS.between(req.getCheckinExpected(), req.getCheckoutExpected());
        BigDecimal estimated = room.getPricePerNight().multiply(BigDecimal.valueOf(days));

        Reservation reservation = Reservation.builder()
                .guest(guest)
                .room(room)
                .checkinExpected(req.getCheckinExpected())
                .checkoutExpected(req.getCheckoutExpected())
                .status(ReservationStatus.CREATED)
                .numGuests(req.getNumGuests())
                .estimatedAmount(estimated)
                .build();

        return mapper.toResponse(reservationRepository.save(reservation));
    }

    // Update dates (only while CREATED)

    @Transactional
    public ReservationResponse updateDates(String id, ReservationRequest req) {
        Reservation reservation = getOrThrow(id);

        if (reservation.getStatus() != ReservationStatus.CREATED) {
            throw new InvalidReservationStateException(
                    "Reservation dates can only be changed while status is CREATED. " +
                    "Current status: " + reservation.getStatus());
        }

        validateDateRange(req.getCheckinExpected(), req.getCheckoutExpected());

        Room room = reservation.getRoom();
        validateCapacity(req.getNumGuests(), room.getCapacity(), room.getNumber());
        validateAvailability(room.getId(), req.getCheckinExpected(), req.getCheckoutExpected(), id);

        long days = ChronoUnit.DAYS.between(req.getCheckinExpected(), req.getCheckoutExpected());
        BigDecimal estimated = room.getPricePerNight().multiply(BigDecimal.valueOf(days));

        reservation.setCheckinExpected(req.getCheckinExpected());
        reservation.setCheckoutExpected(req.getCheckoutExpected());
        reservation.setNumGuests(req.getNumGuests());
        reservation.setEstimatedAmount(estimated);

        return mapper.toResponse(reservationRepository.save(reservation));
    }

    // Check-in  →  CREATED → CHECKED_IN

    @Transactional
    public ReservationResponse checkIn(String id) {
        Reservation reservation = getOrThrow(id);

        // Rule 4 – FSM
        if (reservation.getStatus() != ReservationStatus.CREATED) {
            throw new InvalidReservationStateException(
                    "Check-in requires status CREATED. Current: " + reservation.getStatus());
        }

        // Rule 5 – check-in window: allowed on or after the expected date
        LocalDate today = LocalDate.now();
        if (today.isBefore(reservation.getCheckinExpected())) {
            throw new CheckinWindowException(
                    "Check-in is only allowed on or after the expected date ("
                    + reservation.getCheckinExpected() + "). Today is " + today + ".");
        }

        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservation.setCheckinAt(LocalDateTime.now());

        return mapper.toResponse(reservationRepository.save(reservation));
    }

    // Check-out  →  CHECKED_IN → CHECKED_OUT

    @Transactional
    public ReservationResponse checkOut(String id) {
        Reservation reservation = getOrThrow(id);

        // Rule 4 – FSM
        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new InvalidReservationStateException(
                    "Check-out requires status CHECKED_IN. Current: " + reservation.getStatus());
        }

        LocalDateTime checkoutTime = LocalDateTime.now();

        // Rule 6 – finalAmount = max(1, days) × pricePerNight
        long days = ChronoUnit.DAYS.between(
                reservation.getCheckinAt().toLocalDate(),
                checkoutTime.toLocalDate());
        days = Math.max(1L, days);
        BigDecimal finalAmount = reservation.getRoom().getPricePerNight()
                .multiply(BigDecimal.valueOf(days));

        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        reservation.setCheckoutAt(checkoutTime);
        reservation.setFinalAmount(finalAmount);

        return mapper.toResponse(reservationRepository.save(reservation));
    }

    // Cancel  →  CREATED → CANCELED

    @Transactional
    public ReservationResponse cancel(String id) {
        Reservation reservation = getOrThrow(id);

        // Rule 4 – FSM: can only cancel if still CREATED
        if (reservation.getStatus() != ReservationStatus.CREATED) {
            throw new InvalidReservationStateException(
                    "Cancellation is only allowed while status is CREATED. " +
                    "Current: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.CANCELED);

        return mapper.toResponse(reservationRepository.save(reservation));
    }

    // helpers

    private void validateDateRange(LocalDate checkin, LocalDate checkout) {
        if (!checkout.isAfter(checkin)) {
            throw new InvalidDateRangeException(
                    "checkoutExpected (" + checkout + ") must be after checkinExpected (" + checkin + ").");
        }
    }

    private void validateCapacity(int numGuests, int capacity, int roomNumber) {
        if (numGuests > capacity) {
            throw new CapacityExceededException(
                    "numGuests (" + numGuests + ") exceeds room " + roomNumber
                    + " capacity (" + capacity + ").");
        }
    }

    private void validateAvailability(String roomId, LocalDate checkin,
                                      LocalDate checkout, String excludeId) {
        List<Reservation> overlapping =
                reservationRepository.findOverlapping(roomId, checkin, checkout, excludeId);
        if (!overlapping.isEmpty()) {
            throw new RoomUnavailableException(
                    "Room is unavailable for the period " + checkin + " to " + checkout +
                    ". There is already an active reservation overlapping this range.");
        }
    }

    private Reservation getOrThrow(String id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found with id: " + id));
    }
}
