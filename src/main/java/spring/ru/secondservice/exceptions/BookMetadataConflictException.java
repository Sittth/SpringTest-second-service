package spring.ru.secondservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookMetadataConflictException extends RuntimeException {

    public BookMetadataConflictException(String message) {
        super(message);
    }
}