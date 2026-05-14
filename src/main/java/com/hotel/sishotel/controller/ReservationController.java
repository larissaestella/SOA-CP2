package com.hotel.sishotel.controller;

import com.hotel.sishotel.dto.request.ReservationRequest;
import com.hotel.sishotel.dto.response.ReservationResponse;
import com.hotel.sishotel.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "3. Reservations", description = "Ciclo de vida: criar → check-in → check-out | cancelar")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @Operation(summary = "Listar todas as reservas")
    public ResponseEntity<List<ReservationResponse>> findAll() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reserva por ID")
    public ResponseEntity<ReservationResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(reservationService.findById(id));
    }

    @GetMapping("/guest/{guestId}")
    @Operation(summary = "Listar reservas de um hóspede")
    public ResponseEntity<List<ReservationResponse>> findByGuest(@PathVariable String guestId) {
        return ResponseEntity.ok(reservationService.findByGuest(guestId));
    }

    @GetMapping("/room/{roomId}")
    @Operation(summary = "Listar reservas de um quarto")
    public ResponseEntity<List<ReservationResponse>> findByRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(reservationService.findByRoom(roomId));
    }

    @PostMapping
    @Operation(
        summary = "Criar reserva",
        description = """
            Regras validadas:
            - checkoutExpected > checkinExpected (400)
            - numGuests ≤ capacidade do quarto (400)
            - Sem sobreposição de período no quarto (409)
            - Quarto deve estar ATIVO (409)
            """)
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(req));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar datas da reserva",
        description = "Permitido somente enquanto status = CREATED. Recalcula valorEstimado.")
    public ResponseEntity<ReservationResponse> updateDates(@PathVariable String id,
                                                           @Valid @RequestBody ReservationRequest req) {
        return ResponseEntity.ok(reservationService.updateDates(id, req));
    }

    @PatchMapping("/{id}/checkin")
    @Operation(
        summary = "Realizar check-in (CREATED → CHECKED_IN)",
        description = "Permitido somente na data prevista ou após (422 se antecipado).")
    public ResponseEntity<ReservationResponse> checkIn(@PathVariable String id) {
        return ResponseEntity.ok(reservationService.checkIn(id));
    }

    @PatchMapping("/{id}/checkout")
    @Operation(
        summary = "Realizar check-out (CHECKED_IN → CHECKED_OUT)",
        description = "Calcula valorFinal = max(1, dias) × preçoDiária.")
    public ResponseEntity<ReservationResponse> checkOut(@PathVariable String id) {
        return ResponseEntity.ok(reservationService.checkOut(id));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(
        summary = "Cancelar reserva (CREATED → CANCELED)",
        description = "Permitido somente enquanto status = CREATED. Após check-in, retorna 409.")
    public ResponseEntity<ReservationResponse> cancel(@PathVariable String id) {
        return ResponseEntity.ok(reservationService.cancel(id));
    }
}
