package spring.ru.secondservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import spring.ru.secondservice.dto.create.BookMetadataCreateRequest;
import spring.ru.secondservice.dto.response.BookMetadataResponse;
import spring.ru.secondservice.models.BookMetadataModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMetadataMapper {

    BookMetadataResponse toResponse(BookMetadataModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "idempotencyKey", ignore = true)
    BookMetadataModel toEntity(BookMetadataCreateRequest requestCreate);
}
