package com.hotel.sishotel.service;

import com.hotel.sishotel.domain.exception.DuplicateResourceException;
import com.hotel.sishotel.domain.exception.ResourceNotFoundException;
import com.hotel.sishotel.domain.model.Guest;
import com.hotel.sishotel.dto.request.GuestRequest;
import com.hotel.sishotel.dto.response.GuestResponse;
import com.hotel.sishotel.mapper.GuestMapper;
import com.hotel.sishotel.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final GuestMapper guestMapper;

    @Transactional(readOnly = true)
    public List<GuestResponse> findAll() {
        return guestRepository.findAll().stream()
                .map(guestMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GuestResponse findById(String id) {
        return guestMapper.toResponse(getOrThrow(id));
    }

    @Transactional
    public GuestResponse create(GuestRequest req) {
        if (guestRepository.existsByDocument(req.getDocument())) {
            throw new DuplicateResourceException(
                    "Guest with document '" + req.getDocument() + "' already exists.");
        }
        if (guestRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException(
                    "Guest with email '" + req.getEmail() + "' already exists.");
        }
        Guest saved = guestRepository.save(guestMapper.toEntity(req));
        return guestMapper.toResponse(saved);
    }

    @Transactional
    public GuestResponse update(String id, GuestRequest req) {
        Guest guest = getOrThrow(id);

        guestRepository.findByDocument(req.getDocument()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateResourceException(
                        "Document '" + req.getDocument() + "' is already in use by another guest.");
            }
        });
        guestRepository.findByEmail(req.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateResourceException(
                        "Email '" + req.getEmail() + "' is already in use by another guest.");
            }
        });

        guestMapper.updateFromRequest(guest, req);
        return guestMapper.toResponse(guestRepository.save(guest));
    }

    @Transactional
    public void delete(String id) {
        guestRepository.delete(getOrThrow(id));
    }

    public Guest getOrThrow(String id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Guest not found with id: " + id));
    }
}
