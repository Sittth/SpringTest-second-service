package spring.ru.secondservice.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "book_metadata")
@Getter
@Setter
public class BookMetadataModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID bookId;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(unique = true)
    private UUID idempotencyKey;
}
