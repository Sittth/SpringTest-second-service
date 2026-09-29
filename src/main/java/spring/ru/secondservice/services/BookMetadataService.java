package spring.ru.secondservice.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.secondservice.dto.create.BookMetadataCreateRequest;
import spring.ru.secondservice.dto.response.BookMetadataResponse;
import spring.ru.secondservice.mapper.BookMetadataMapper;
import spring.ru.secondservice.models.BookMetadataModel;
import spring.ru.secondservice.repositories.BookMetadataRepository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookMetadataService {

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

        Optional<BookMetadataModel> existing = repository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            log.info("Idempotency key {} already processed, returning existing metadata", idempotencyKey);
            return bookMetadataMapper.toResponse(existing.get());
        }

        BookMetadataModel entity = bookMetadataMapper.toEntity(request);
        entity.setIdempotencyKey(idempotencyKey);

        try {
            BookMetadataModel saved = repository.save(entity);
            log.info("Saved book metadata with id {}", saved.getId());
            return bookMetadataMapper.toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            log.warn("Race condition detected for idempotency key {}, fetching existing", idempotencyKey);
            return repository.findByIdempotencyKey(idempotencyKey)
                    .map(bookMetadataMapper::toResponse)
                    .orElseThrow(() -> e);
        }
    }
}
