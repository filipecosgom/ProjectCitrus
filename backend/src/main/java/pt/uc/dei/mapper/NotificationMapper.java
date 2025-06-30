package pt.uc.dei.mapper;

import org.mapstruct.*;
import pt.uc.dei.dtos.NotificationDTO;
import pt.uc.dei.entities.NotificationEntity;

@Mapper(componentModel = "jakarta")
public interface NotificationMapper {
    NotificationDTO toDto(NotificationEntity entity);
    NotificationEntity toEntity(NotificationDTO dto);
}
