package spring.ru.secondservice.dto.response;

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
 * BookMetadataResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-06T16:53:38.052516800+03:00[Europe/Moscow]", comments = "Generator version: 7.8.0")
public class BookMetadataResponse {

  private UUID id;

  private UUID bookId;

  private String publisher;

  private BigDecimal price;

  public BookMetadataResponse id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @Valid 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public BookMetadataResponse bookId(UUID bookId) {
    this.bookId = bookId;
    return this;
  }

  /**
   * Get bookId
   * @return bookId
   */
  @Valid 
  @Schema(name = "bookId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookId")
  public UUID getBookId() {
    return bookId;
  }

  public void setBookId(UUID bookId) {
    this.bookId = bookId;
  }

  public BookMetadataResponse publisher(String publisher) {
    this.publisher = publisher;
    return this;
  }

  /**
   * Get publisher
   * @return publisher
   */
  
  @Schema(name = "publisher", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("publisher")
  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public BookMetadataResponse price(BigDecimal price) {
    this.price = price;
    return this;
  }

  /**
   * Get price
   * @return price
   */
  @Valid 
  @Schema(name = "price", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    BookMetadataResponse bookMetadataResponse = (BookMetadataResponse) o;
    return Objects.equals(this.id, bookMetadataResponse.id) &&
        Objects.equals(this.bookId, bookMetadataResponse.bookId) &&
        Objects.equals(this.publisher, bookMetadataResponse.publisher) &&
        Objects.equals(this.price, bookMetadataResponse.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, bookId, publisher, price);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookMetadataResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

