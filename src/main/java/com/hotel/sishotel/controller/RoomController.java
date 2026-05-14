package com.hotel.sishotel.controller;

import com.hotel.sishotel.dto.request.RoomRequest;
import com.hotel.sishotel.dto.response.RoomResponse;
import com.hotel.sishotel.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "2. Rooms", description = "CRUD de quartos")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    @Operation(summary = "Listar todos os quartos")
    public ResponseEntity<List<RoomResponse>> findAll() {
        return ResponseEntity.ok(roomService.findAll());
    }

    @GetMapping("/active")
    @Operation(summary = "Listar quartos ativos")
    public ResponseEntity<List<RoomResponse>> findActive() {
        return ResponseEntity.ok(roomService.findActive());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar quarto por ID")
    public ResponseEntity<RoomResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(roomService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo quarto")
    public ResponseEntity<RoomResponse> create(@Valid @RequestBody RoomRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.create(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar quarto")
    public ResponseEntity<RoomResponse> update(@PathVariable String id,
                                               @Valid @RequestBody RoomRequest req) {
        return ResponseEntity.ok(roomService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar quarto (soft-delete — INATIVO)",
               description = "Não exclui fisicamente se houver reservas ativas. Retorna 409.")
    public ResponseEntity<Void> deactivate(@PathVariable String id) {
        roomService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Reativar quarto (ATIVO)")
    public ResponseEntity<RoomResponse> activate(@PathVariable String id) {
        return ResponseEntity.ok(roomService.activate(id));
    }
}
