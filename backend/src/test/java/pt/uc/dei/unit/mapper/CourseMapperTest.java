package pt.uc.dei.unit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pt.uc.dei.dtos.CourseDTO;
import pt.uc.dei.dtos.CourseNewDTO;
import pt.uc.dei.dtos.CourseUpdateDTO;
import pt.uc.dei.entities.CourseEntity;
import pt.uc.dei.enums.CourseArea;
import pt.uc.dei.enums.Language;
import pt.uc.dei.mapper.CourseMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseMapperTest {
    private CourseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CourseMapper.class);
    }

    @Test
    void testToDto() {
        CourseEntity entity = new CourseEntity();
        entity.setId(1L);
        entity.setTitle("Java Basics");
        entity.setCreationDate(LocalDate.of(2024, 1, 1));
        entity.setDuration(40);
        entity.setLanguage(Language.ENGLISH);
        entity.setArea(CourseArea.BACKEND);
        entity.setDescription("Intro to Java");
        entity.setLink("http://example.com/java");
        entity.setCourseHasImage(true);
        entity.setCourseIsActive(true);

        CourseDTO dto = mapper.toDto(entity);
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getTitle(), dto.getTitle());
        assertEquals(entity.getCreationDate(), dto.getCreationDate());
        assertEquals(entity.getDuration(), dto.getDuration());
        assertEquals(entity.getLanguage(), dto.getLanguage());
        assertEquals(entity.getArea(), dto.getArea());
        assertEquals(entity.getDescription(), dto.getDescription());
        assertEquals(entity.getLink(), dto.getLink());
        assertEquals(entity.getCourseHasImage(), dto.getCourseHasImage());
        assertEquals(entity.getCourseIsActive(), dto.getCourseIsActive());
    }

    @Test
    void testToEntityFromDTO() {
        CourseDTO dto = new CourseDTO();
        dto.setId(2L);
        dto.setTitle("Python Basics");
        dto.setCreationDate(LocalDate.of(2025, 2, 2));
        dto.setDuration(30);
        dto.setLanguage(Language.PORTUGUESE);
        dto.setArea(CourseArea.FRONTEND);
        dto.setDescription("Intro to Python");
        dto.setLink("http://example.com/python");
        dto.setCourseHasImage(false);
        dto.setCourseIsActive(false);

        CourseEntity entity = mapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getCreationDate(), entity.getCreationDate());
        assertEquals(dto.getDuration(), entity.getDuration());
        assertEquals(dto.getLanguage(), entity.getLanguage());
        assertEquals(dto.getArea(), entity.getArea());
        assertEquals(dto.getDescription(), entity.getDescription());
        assertEquals(dto.getLink(), entity.getLink());
        assertEquals(dto.getCourseHasImage(), entity.getCourseHasImage());
        assertEquals(dto.getCourseIsActive(), entity.getCourseIsActive());
    }

    @Test
    void testToEntityFromNewDTO() {
        CourseNewDTO newDto = new CourseNewDTO();
        newDto.setTitle("C++ Basics");
        newDto.setCreationDate(LocalDate.of(2023, 3, 3));
        newDto.setDuration(25);
        newDto.setLanguage(Language.ENGLISH);
        newDto.setArea(CourseArea.INFRASTRUCTURE);
        newDto.setDescription("Intro to C++");
        newDto.setLink("http://example.com/cpp");
        newDto.setCourseHasImage(true);
        newDto.setCourseIsActive(true);

        CourseEntity entity = mapper.toEntity(newDto);
        assertNotNull(entity);
        assertEquals(newDto.getTitle(), entity.getTitle());
        assertEquals(newDto.getCreationDate(), entity.getCreationDate());
        assertEquals(newDto.getDuration(), entity.getDuration());
        assertEquals(newDto.getLanguage(), entity.getLanguage());
        assertEquals(newDto.getArea(), entity.getArea());
        assertEquals(newDto.getDescription(), entity.getDescription());
        assertEquals(newDto.getLink(), entity.getLink());
        assertEquals(newDto.getCourseHasImage(), entity.getCourseHasImage());
        assertEquals(newDto.getCourseIsActive(), entity.getCourseIsActive());
    }

    @Test
    void testToEntityFromUpdateDTO() {
        CourseUpdateDTO updateDto = new CourseUpdateDTO();
        updateDto.setId(3L);
        updateDto.setTitle("JS Basics");
        updateDto.setCreationDate(LocalDate.of(2022, 4, 4));
        updateDto.setDuration(20);
        updateDto.setLanguage(Language.ENGLISH);
        updateDto.setArea(CourseArea.UX_UI);
        updateDto.setDescription("Intro to JS");
        updateDto.setLink("http://example.com/js");
        updateDto.setCourseHasImage(false);
        updateDto.setCourseIsActive(false);

        CourseEntity entity = mapper.toEntity(updateDto);
        assertNotNull(entity);
        assertEquals(updateDto.getTitle(), entity.getTitle());
        assertEquals(updateDto.getCreationDate(), entity.getCreationDate());
        assertEquals(updateDto.getDuration(), entity.getDuration());
        assertEquals(updateDto.getLanguage(), entity.getLanguage());
        assertEquals(updateDto.getArea(), entity.getArea());
        assertEquals(updateDto.getDescription(), entity.getDescription());
        assertEquals(updateDto.getLink(), entity.getLink());
        assertEquals(updateDto.getCourseHasImage(), entity.getCourseHasImage());
        assertEquals(updateDto.getCourseIsActive(), entity.getCourseIsActive());
    }

    @Test
    void testToDtoList() {
        CourseEntity entity1 = new CourseEntity();
        entity1.setTitle("A");
        CourseEntity entity2 = new CourseEntity();
        entity2.setTitle("B");
        List<CourseEntity> entities = Arrays.asList(entity1, entity2);
        List<CourseDTO> dtos = mapper.toDtoList(entities);
        assertEquals(2, dtos.size());
        assertEquals("A", dtos.get(0).getTitle());
        assertEquals("B", dtos.get(1).getTitle());
    }

    @Test
    void testToEntityList() {
        CourseDTO dto1 = new CourseDTO();
        dto1.setTitle("X");
        CourseDTO dto2 = new CourseDTO();
        dto2.setTitle("Y");
        List<CourseDTO> dtos = Arrays.asList(dto1, dto2);
        List<CourseEntity> entities = mapper.toEntityList(dtos);
        assertEquals(2, entities.size());
        assertEquals("X", entities.get(0).getTitle());
        assertEquals("Y", entities.get(1).getTitle());
    }

    @Test
    void testUpdateEntityFromDto() {
        CourseEntity entity = new CourseEntity();
        entity.setTitle("Old");
        entity.setDuration(10);
        entity.setLanguage(Language.ENGLISH);
        entity.setArea(CourseArea.BACKEND);
        entity.setDescription("Old desc");
        entity.setLink("old");
        entity.setCourseHasImage(false);
        entity.setCourseIsActive(false);

        CourseDTO dto = new CourseDTO();
        dto.setTitle("Updated");
        dto.setDuration(99);
        dto.setLanguage(Language.PORTUGUESE);
        dto.setArea(CourseArea.FRONTEND);
        dto.setDescription("Updated desc");
        dto.setLink("updated");
        dto.setCourseHasImage(true);
        dto.setCourseIsActive(true);

        mapper.updateEntityFromDto(dto, entity);
        assertEquals("Updated", entity.getTitle());
        assertEquals(99, entity.getDuration());
        assertEquals(Language.PORTUGUESE, entity.getLanguage());
        assertEquals(CourseArea.FRONTEND, entity.getArea());
        assertEquals("Updated desc", entity.getDescription());
        assertEquals("updated", entity.getLink());
        assertEquals(true, entity.getCourseHasImage());
        assertEquals(true, entity.getCourseIsActive());
    }

    @Test
    void testUpdateEntityFromUpdateDto() {
        CourseEntity entity = new CourseEntity();
        entity.setTitle("Old");
        entity.setDuration(10);
        entity.setLanguage(Language.ENGLISH);
        entity.setArea(CourseArea.BACKEND);
        entity.setDescription("Old desc");
        entity.setLink("old");
        entity.setCourseHasImage(false);
        entity.setCourseIsActive(false);

        CourseUpdateDTO updateDto = new CourseUpdateDTO();
        updateDto.setTitle("Updated");
        updateDto.setDuration(99);
        updateDto.setLanguage(Language.PORTUGUESE);
        updateDto.setArea(CourseArea.FRONTEND);
        updateDto.setDescription("Updated desc");
        updateDto.setLink("updated");
        updateDto.setCourseHasImage(true);
        updateDto.setCourseIsActive(true);

        mapper.updateEntityFromUpdateDto(updateDto, entity);
        assertEquals("Updated", entity.getTitle());
        assertEquals(99, entity.getDuration());
        assertEquals(Language.PORTUGUESE, entity.getLanguage());
        assertEquals(CourseArea.FRONTEND, entity.getArea());
        assertEquals("Updated desc", entity.getDescription());
        assertEquals("updated", entity.getLink());
        assertEquals(true, entity.getCourseHasImage());
        assertEquals(true, entity.getCourseIsActive());
    }

    @Test
    void testNullAndEmptyListMappings() {
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity((CourseDTO) null));
        assertNull(mapper.toEntity((CourseNewDTO) null));
        assertNull(mapper.toEntity((CourseUpdateDTO) null));
        assertTrue(mapper.toDtoList(null).isEmpty());
        assertTrue(mapper.toEntityList(null).isEmpty());
        assertTrue(mapper.toDtoList(Collections.emptyList()).isEmpty());
        assertTrue(mapper.toEntityList(Collections.emptyList()).isEmpty());
    }
}
