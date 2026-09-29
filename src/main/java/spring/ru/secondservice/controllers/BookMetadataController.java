package spring.ru.secondservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import spring.ru.secondservice.api.BookMetadataApi;
import spring.ru.secondservice.dto.create.BookMetadataCreateRequest;
import spring.ru.secondservice.dto.response.BookMetadataResponse;
import spring.ru.secondservice.services.BookMetadataService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BookMetadataController implements BookMetadataApi {

    private final BookMetadataService bookMetadataService;

    @Override
    public BookMetadataResponse createBookMetadata(UUID idempotencyKey,
                                                   BookMetadataCreateRequest bookMetadataCreateRequest) {
        return bookMetadataService.create(idempotencyKey, bookMetadataCreateRequest);
    }

    @Override
    public BookMetadataResponse getBookMetadataByBookId(UUID bookId) {
        return bookMetadataService.getByBookId(bookId);
    }
}
