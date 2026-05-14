package com.hotel.sishotel.controller;

import com.hotel.sishotel.dto.request.GuestRequest;
import com.hotel.sishotel.dto.response.GuestResponse;
import com.hotel.sishotel.service.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guests")
@RequiredArgsConstructor
@Tag(name = "1. Guests", description = "CRUD de hóspedes")
public class GuestController {

    private final GuestService guestService;

    @GetMapping
    @Operation(summary = "Listar todos os hóspedes")
    public ResponseEntity<List<GuestResponse>> findAll() {
        return ResponseEntity.ok(guestService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar hóspede por ID")
    public ResponseEntity<GuestResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(guestService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo hóspede")
    public ResponseEntity<GuestResponse> create(@Valid @RequestBody GuestRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.create(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar hóspede")
    public ResponseEntity<GuestResponse> update(@PathVariable String id,
                                                @Valid @RequestBody GuestRequest req) {
        return ResponseEntity.ok(guestService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover hóspede")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        guestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
