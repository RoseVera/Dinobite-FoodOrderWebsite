package com.dinobite.dinobitebackend.mapper;

import com.dinobite.dinobitebackend.dto.CommentRequestDto;
import com.dinobite.dinobitebackend.dto.CommentResponseDto;
import com.dinobite.dinobitebackend.model.Comment;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper interface for converting between Comment entities and DTOs.
 * This interface uses MapStruct to generate the implementation at compile time.
 * It defines methods for converting between CommentRequestDto, CommentResponseDto, and Comment entities.
 */
@Mapper(componentModel = "spring",
        uses = {OrderMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CommentMapper {

    /**
     * Maps a Comment entity to a CommentResponseDto.
     * Extracts the order's ID from the associated Order entity.
     *
     * @param comment the Comment entity.
     * @return the mapped CommentResponseDto.
     */
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    CommentResponseDto toResponseDto(Comment comment);

    /**
     * Maps a CommentRequestDto to a Comment entity.
     * Ignores the ID field in the request DTO and maps the order ID to the Order entity.
     *
     * @param requestDto the CommentRequestDto.
     * @return the mapped Comment entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", source = "orderId", qualifiedByName = "idToOrder")
    @Mapping(target = "createdAt", ignore = true)
    Comment toEntity(CommentRequestDto requestDto);

    /**
     * Updates an existing Comment entity with values from a CommentRequestDto.
     * Ignores the ID to preserve the existing entity's identity.
     *
     * @param dto the CommentRequestDto.
     * @param entity the existing Comment entity to update.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(CommentRequestDto dto, @MappingTarget Comment entity);

    /**
     * Maps a list of Comment entities to a list of CommentResponseDto.
     *
     * @param comments the list of Comment entities.
     * @return the list of mapped CommentResponseDto.
     */
    List<CommentResponseDto> toResponseDtoList(List<Comment> comments);

    /**
     * Helper method to create a Comment entity reference from an ID.
     * This is used to avoid loading the entire Comment entity when only the ID is needed.
     *
     * @param id the ID of the Comment.
     * @return a Comment entity with only the ID field set.
     */
    @Named("idToComment")
    default Comment idToComment(Integer id) {
        if (id == null) return null;
        return Comment.builder().id(id).build();
    }
}