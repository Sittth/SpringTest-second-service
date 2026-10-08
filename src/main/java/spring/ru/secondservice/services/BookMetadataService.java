package spring.ru.secondservice.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.secondservice.dto.create.BookMetadataCreateRequest;
import spring.ru.secondservice.dto.response.BookMetadataResponse;
import spring.ru.secondservice.exceptions.BookMetadataConflictException;
import spring.ru.secondservice.mapper.BookMetadataMapper;
import spring.ru.secondservice.models.BookMetadataModel;
import spring.ru.secondservice.repositories.BookMetadataRepository;

import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookMetadataService {

    private static final int PRICE_SCALE = 2;

    private final BookMetadataRepository repository;
    private final BookMetadataMapper bookMetadataMapper;

    public BookMetadataResponse getByBookId(UUID bookId) {

        log.info("Finding book metadata by bookId: {}", bookId);

        BookMetadataModel entity = repository.findByBookId(bookId)
                .orElseThrow(() -> {
                    log.error("Book metadata not found for bookId {}", bookId);
                    return new EntityNotFoundException("Metadata not found for book: " + bookId);
                });

        return bookMetadataMapper.toResponse(entity);
    }

    @Transactional
    public BookMetadataResponse create(UUID idempotencyKey, BookMetadataCreateRequest request) {

        log.info("Creating book metadata: {}", request);

        int inserted = repository.insertIfAbsent(
                request.getBookId(),
                request.getPublisher(),
                request.getPrice(),
                idempotencyKey
        );

        BookMetadataModel stored = repository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> {
                    log.warn("Book {} already has metadata registered under a different idempotency key",
                            request.getBookId());
                    return new BookMetadataConflictException(
                            "Metadata for book " + request.getBookId()
                                    + " is already registered under a different idempotency key");
                });

        if (inserted == 1) {
            log.info("Saved book metadata with id {}", stored.getId());
            return bookMetadataMapper.toResponse(stored);
        }

        if (!hasSamePayload(stored, request)) {
            log.warn("Idempotency key {} reused with a different payload", idempotencyKey);
            throw new BookMetadataConflictException(
                    "Idempotency key " + idempotencyKey + " was already used with a different request payload");
        }

        log.info("Idempotency key {} already processed, returning existing metadata", idempotencyKey);
        return bookMetadataMapper.toResponse(stored);
    }

    private boolean hasSamePayload(BookMetadataModel stored, BookMetadataCreateRequest request) {
        return stored.getBookId().equals(request.getBookId())
                && stored.getPublisher().equals(request.getPublisher())
                && stored.getPrice().compareTo(request.getPrice().setScale(PRICE_SCALE, RoundingMode.HALF_UP)) == 0;
    }
}
