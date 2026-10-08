package spring.ru.secondservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.ru.secondservice.models.BookMetadataModel;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface BookMetadataRepository extends JpaRepository<BookMetadataModel, UUID> {

    Optional<BookMetadataModel> findByBookId(UUID bookId);

    Optional<BookMetadataModel> findByIdempotencyKey(UUID idempotencyKey);

    @Modifying
    @Query(value = """
        INSERT INTO test_metadata.book_metadata (id, book_id, publisher, price, idempotency_key)
        VALUES (gen_random_uuid(), :bookId, :publisher, :price, :idempotencyKey)
        ON CONFLICT DO NOTHING
        """, nativeQuery = true)
    int insertIfAbsent(@Param("bookId") UUID bookId,
                       @Param("publisher") String publisher,
                       @Param("price") BigDecimal price,
                       @Param("idempotencyKey") UUID idempotencyKey);
}
