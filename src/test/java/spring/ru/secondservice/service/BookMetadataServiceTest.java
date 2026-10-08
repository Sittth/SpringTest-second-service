package spring.ru.secondservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import spring.ru.secondservice.AbstractServiceTest;
import spring.ru.secondservice.dto.create.BookMetadataCreateRequest;
import spring.ru.secondservice.dto.response.BookMetadataResponse;
import spring.ru.secondservice.exceptions.BookMetadataConflictException;
import spring.ru.secondservice.repositories.BookMetadataRepository;
import spring.ru.secondservice.services.BookMetadataService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookMetadataServiceTest extends AbstractServiceTest {

    @Autowired
    BookMetadataService bookMetadataService;

    @Autowired
    BookMetadataRepository repository;

    @Test
    void create_shouldPersistMetadata_whenKeyIsNew() {
        UUID bookId = UUID.randomUUID();

        BookMetadataResponse response = bookMetadataService.create(
                UUID.randomUUID(), request(bookId, "Secker & Warburg", "12.99"));

        assertThat(response.getId()).isNotNull();
        assertThat(response.getBookId()).isEqualTo(bookId);
        assertThat(response.getPublisher()).isEqualTo("Secker & Warburg");
        assertThat(response.getPrice()).isEqualByComparingTo("12.99");
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void create_shouldReturnExistingMetadata_whenSameKeyAndSamePayload() {
        UUID key = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        BookMetadataResponse first = bookMetadataService.create(key, request(bookId, "Publisher", "10.00"));
        BookMetadataResponse second = bookMetadataService.create(key, request(bookId, "Publisher", "10.00"));

        assertThat(second.getId()).isEqualTo(first.getId());
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void create_shouldTreatDifferentPriceScaleAsSamePayload() {
        UUID key = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        BookMetadataResponse first = bookMetadataService.create(key, request(bookId, "Publisher", "10"));
        BookMetadataResponse second = bookMetadataService.create(key, request(bookId, "Publisher", "10.00"));

        assertThat(second.getId()).isEqualTo(first.getId());
    }

    @Test
    void create_shouldTreatPriceRoundedByDatabaseAsSamePayload() {
        UUID key = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        BookMetadataResponse first = bookMetadataService.create(key, request(bookId, "Publisher", "12.999"));
        BookMetadataResponse second = bookMetadataService.create(key, request(bookId, "Publisher", "12.999"));

        assertThat(second.getId()).isEqualTo(first.getId());
    }

    @Test
    void create_shouldThrowConflict_whenSameKeyButDifferentPublisher() {
        UUID key = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        bookMetadataService.create(key, request(bookId, "Publisher A", "10.00"));

        assertThatThrownBy(() -> bookMetadataService.create(key, request(bookId, "Publisher B", "10.00")))
                .isInstanceOf(BookMetadataConflictException.class);

        assertThat(repository.findByIdempotencyKey(key).orElseThrow().getPublisher()).isEqualTo("Publisher A");
    }

    @Test
    void create_shouldThrowConflict_whenSameKeyButDifferentPrice() {
        UUID key = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        bookMetadataService.create(key, request(bookId, "Publisher", "10.00"));

        assertThatThrownBy(() -> bookMetadataService.create(key, request(bookId, "Publisher", "11.00")))
                .isInstanceOf(BookMetadataConflictException.class);
    }

    @Test
    void create_shouldThrowConflict_whenSameKeyButDifferentBookId() {
        UUID key = UUID.randomUUID();
        bookMetadataService.create(key, request(UUID.randomUUID(), "Publisher", "10.00"));

        assertThatThrownBy(() -> bookMetadataService.create(key, request(UUID.randomUUID(), "Publisher", "10.00")))
                .isInstanceOf(BookMetadataConflictException.class);

        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void create_shouldThrowConflict_whenBookAlreadyRegisteredUnderAnotherKey() {
        UUID bookId = UUID.randomUUID();
        bookMetadataService.create(UUID.randomUUID(), request(bookId, "Publisher", "10.00"));

        assertThatThrownBy(() -> bookMetadataService.create(UUID.randomUUID(), request(bookId, "Publisher", "10.00")))
                .isInstanceOf(BookMetadataConflictException.class);

        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void create_shouldCreateSingleRow_whenSameKeyIsSentConcurrently() throws Exception {
        int threads = 8;
        UUID key = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);

        try {
            List<Callable<BookMetadataResponse>> tasks = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                tasks.add(() -> {
                    ready.countDown();
                    start.await();
                    return bookMetadataService.create(key, request(bookId, "Publisher", "10.00"));
                });
            }

            List<Future<BookMetadataResponse>> futures = new ArrayList<>();
            for (Callable<BookMetadataResponse> task : tasks) {
                futures.add(executor.submit(task));
            }

            ready.await();
            start.countDown();

            List<UUID> ids = new ArrayList<>();
            for (Future<BookMetadataResponse> future : futures) {
                ids.add(future.get().getId());
            }

            assertThat(ids).allMatch(id -> id.equals(ids.get(0)));
            assertThat(repository.count()).isEqualTo(1);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void getByBookId_shouldReturnMetadata_whenExists() {
        UUID bookId = UUID.randomUUID();
        bookMetadataService.create(UUID.randomUUID(), request(bookId, "Publisher", "10.00"));

        BookMetadataResponse response = bookMetadataService.getByBookId(bookId);

        assertThat(response.getBookId()).isEqualTo(bookId);
        assertThat(response.getPublisher()).isEqualTo("Publisher");
    }

    @Test
    void getByBookId_shouldThrow_whenNotFound() {
        assertThatThrownBy(() -> bookMetadataService.getByBookId(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private BookMetadataCreateRequest request(UUID bookId, String publisher, String price) {
        return new BookMetadataCreateRequest(bookId, publisher, new BigDecimal(price));
    }
}