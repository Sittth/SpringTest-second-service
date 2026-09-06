package spring.ru.secondservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.ru.secondservice.models.BookMetadataModel;

import java.util.Optional;
import java.util.UUID;

public interface BookMetadataRepository extends JpaRepository<BookMetadataModel, UUID> {

    Optional<BookMetadataModel> findByBookId(UUID bookId);
    Optional<BookMetadataModel> findByIdempotencyKey(UUID idempotencyKey);
}
