package pt.uc.dei.mapper;

import org.mapstruct.*;
import pt.uc.dei.dtos.MessageDTO;
import pt.uc.dei.entities.MessageEntity;

@Mapper(componentModel = "jakarta")
public interface MessageMapper {
    MessageDTO toDto(MessageEntity entity);
    MessageEntity toEntity(MessageDTO dto);
}
