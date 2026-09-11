package spring.ru.secondservice.dto.create;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BookMetadataCreateRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-06T16:53:37.856703400+03:00[Europe/Moscow]", comments = "Generator version: 7.8.0")
public class BookMetadataCreateRequest {

  private UUID bookId;

  private String publisher;

  private BigDecimal price;

  public BookMetadataCreateRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BookMetadataCreateRequest(UUID bookId, String publisher, BigDecimal price) {
    this.bookId = bookId;
    this.publisher = publisher;
    this.price = price;
  }

  public BookMetadataCreateRequest bookId(UUID bookId) {
    this.bookId = bookId;
    return this;
  }

  /**
   * Get bookId
   * @return bookId
   */
  @NotNull @Valid 
  @Schema(name = "bookId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bookId")
  public UUID getBookId() {
    return bookId;
  }

  public void setBookId(UUID bookId) {
    this.bookId = bookId;
  }

  public BookMetadataCreateRequest publisher(String publisher) {
    this.publisher = publisher;
    return this;
  }

  /**
   * Get publisher
   * @return publisher
   */
  @NotNull @Size(min = 1, max = 100) 
  @Schema(name = "publisher", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("publisher")
  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public BookMetadataCreateRequest price(BigDecimal price) {
    this.price = price;
    return this;
  }

  /**
   * Get price
   * minimum: 0
   * @return price
   */
  @NotNull @Valid @DecimalMin("0") 
  @Schema(name = "price", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("price")
  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookMetadataCreateRequest bookMetadataCreateRequest = (BookMetadataCreateRequest) o;
    return Objects.equals(this.bookId, bookMetadataCreateRequest.bookId) &&
        Objects.equals(this.publisher, bookMetadataCreateRequest.publisher) &&
        Objects.equals(this.price, bookMetadataCreateRequest.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookId, publisher, price);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookMetadataCreateRequest {\n");
    sb.append("    bookId: ").append(toIndentedString(bookId)).append("\n");
    sb.append("    publisher: ").append(toIndentedString(publisher)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

