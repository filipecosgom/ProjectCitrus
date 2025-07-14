package pt.uc.dei.unit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pt.uc.dei.dtos.CycleDTO;
import pt.uc.dei.dtos.CycleUpdateDTO;
import pt.uc.dei.entities.CycleEntity;
import pt.uc.dei.enums.CycleState;
import pt.uc.dei.mapper.CycleMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CycleMapperTest {
    private CycleMapper mapper;

    @BeforeEach
    void setUp() throws Exception {
        mapper = Mappers.getMapper(CycleMapper.class);
        // Inject mock AppraisalMapper into CycleMapperImpl
        if (mapper.getClass().getSimpleName().equals("CycleMapperImpl")) {
            java.lang.reflect.Field appraisalMapperField = mapper.getClass().getDeclaredField("appraisalMapper");
            appraisalMapperField.setAccessible(true);
            appraisalMapperField.set(mapper, new AppraisalMapperMock());
        }
    }

    @Test
    void testToDto() {
        CycleEntity entity = new CycleEntity();
        entity.setId(1L);
        entity.setStartDate(LocalDate.of(2024, 1, 1));
        entity.setEndDate(LocalDate.of(2024, 12, 31));
        entity.setState(CycleState.OPEN);
        // Admin and evaluations are not mapped in this test

        CycleDTO dto = mapper.toDto(entity);
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getStartDate(), dto.getStartDate());
        assertEquals(entity.getEndDate(), dto.getEndDate());
        assertEquals(entity.getState(), dto.getState());
    }

    @Test
    void testToEntityFromDTO() {
        CycleDTO dto = new CycleDTO();
        dto.setId(2L);
        dto.setStartDate(LocalDate.of(2025, 2, 2));
        dto.setEndDate(LocalDate.of(2025, 11, 30));
        dto.setState(CycleState.CLOSED);
        // AdminId and evaluations are not mapped in this test

        CycleEntity entity = mapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals(dto.getStartDate(), entity.getStartDate());
        assertEquals(dto.getEndDate(), entity.getEndDate());
        // State is ignored in mapping
    }

    @Test
    void testToDtoList() {
        CycleEntity entity1 = new CycleEntity();
        entity1.setStartDate(LocalDate.of(2022, 1, 1));
        CycleEntity entity2 = new CycleEntity();
        entity2.setStartDate(LocalDate.of(2023, 1, 1));
        List<CycleEntity> entities = Arrays.asList(entity1, entity2);
        List<CycleDTO> dtos = mapper.toDtoList(entities);
        assertEquals(2, dtos.size());
        assertEquals(LocalDate.of(2022, 1, 1), dtos.get(0).getStartDate());
        assertEquals(LocalDate.of(2023, 1, 1), dtos.get(1).getStartDate());
    }

    @Test
    void testToEntityList() {
        CycleDTO dto1 = new CycleDTO();
        dto1.setStartDate(LocalDate.of(2020, 5, 5));
        CycleDTO dto2 = new CycleDTO();
        dto2.setStartDate(LocalDate.of(2021, 6, 6));
        List<CycleDTO> dtos = Arrays.asList(dto1, dto2);
        List<CycleEntity> entities = mapper.toEntityList(dtos);
        assertEquals(2, entities.size());
        assertEquals(LocalDate.of(2020, 5, 5), entities.get(0).getStartDate());
        assertEquals(LocalDate.of(2021, 6, 6), entities.get(1).getStartDate());
    }

    @Test
    void testUpdateEntityFromDto() {
        CycleEntity entity = new CycleEntity();
        entity.setStartDate(LocalDate.of(2023, 1, 1));
        entity.setEndDate(LocalDate.of(2023, 12, 31));
        entity.setState(CycleState.OPEN);

        CycleUpdateDTO updateDto = new CycleUpdateDTO();
        updateDto.setStartDate(LocalDate.of(2024, 2, 2));
        updateDto.setEndDate(LocalDate.of(2024, 11, 30));
        updateDto.setState(CycleState.CLOSED);

        mapper.updateEntityFromDto(updateDto, entity);
        assertEquals(LocalDate.of(2024, 2, 2), entity.getStartDate());
        assertEquals(LocalDate.of(2024, 11, 30), entity.getEndDate());
        assertEquals(CycleState.CLOSED, entity.getState());
    }

    @Test
    void testNullAndEmptyListMappings() {
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity((CycleDTO) null));
        assertTrue(mapper.toDtoList(null).isEmpty());
        assertTrue(mapper.toEntityList(null).isEmpty());
        assertTrue(mapper.toDtoList(Collections.emptyList()).isEmpty());
        assertTrue(mapper.toEntityList(Collections.emptyList()).isEmpty());
    }
}
