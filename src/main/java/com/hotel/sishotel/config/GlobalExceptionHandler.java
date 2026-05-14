package com.hotel.sishotel.config;

import com.hotel.sishotel.domain.exception.*;
import com.hotel.sishotel.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest req) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> ErrorResponse.FieldError.builder()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .build())
                .toList();

        return build(HttpStatus.BAD_REQUEST, "ValidationError",
                "Request has validation errors. See fieldErrors.", req, fieldErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                           HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "MalformedRequest",
                "Request body is malformed or missing: " + ex.getMostSpecificCause().getMessage(), req, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "TypeMismatch",
                "Parameter '" + ex.getName() + "' has invalid value: " + ex.getValue(), req, null);
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ErrorResponse> handleDateRange(InvalidDateRangeException ex,
                                                         HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "InvalidDateRangeException", ex.getMessage(), req, null);
    }

    @ExceptionHandler(CapacityExceededException.class)
    public ResponseEntity<ErrorResponse> handleCapacity(CapacityExceededException ex,
                                                        HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "CapacityExceededException", ex.getMessage(), req, null);
    }

    // 404

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex,
                                                        HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "ResourceNotFoundException", ex.getMessage(), req, null);
    }

    // 409

    @ExceptionHandler(RoomUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleRoomUnavailable(RoomUnavailableException ex,
                                                               HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "RoomUnavailableException", ex.getMessage(), req, null);
    }

    @ExceptionHandler(InvalidReservationStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidState(InvalidReservationStateException ex,
                                                            HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "InvalidReservationStateException", ex.getMessage(), req, null);
    }

    @ExceptionHandler(RoomInactiveException.class)
    public ResponseEntity<ErrorResponse> handleRoomInactive(RoomInactiveException ex,
                                                            HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "RoomInactiveException", ex.getMessage(), req, null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex,
                                                         HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "DuplicateResourceException", ex.getMessage(), req, null);
    }

    // ── 422 ──────────────────────────────────────────────────────────────────

    @ExceptionHandler(CheckinWindowException.class)
    public ResponseEntity<ErrorResponse> handleCheckinWindow(CheckinWindowException ex,
                                                             HttpServletRequest req) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "CheckinWindowException", ex.getMessage(), req, null);
    }

    // 500

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "InternalServerError",
                "Unexpected error: " + ex.getMessage(), req, null);
    }

    // helper method to build consistent error responses

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message,
                                                HttpServletRequest req,
                                                List<ErrorResponse.FieldError> fieldErrors) {
        ErrorResponse body = ErrorResponse.builder()
                .status(status.value())
                .error(error)
                .message(message)
                .path(req.getRequestURI())
                .timestamp(LocalDateTime.now())
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
