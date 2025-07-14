package pt.uc.dei.unit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pt.uc.dei.dtos.FinishedCourseDTO;
import pt.uc.dei.entities.CourseEntity;
import pt.uc.dei.entities.FinishedCourseEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.mapper.FinishedCourseMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FinishedCourseMapperTest {
    private FinishedCourseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(FinishedCourseMapper.class);
    }

    @Test
    void testToDto() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@example.com");
        CourseEntity course = new CourseEntity();
        course.setId(2L);
        course.setTitle("Java 101");
        course.setDescription("Intro to Java");
        course.setCreationDate(LocalDate.of(2023, 1, 1));
        course.setDuration(40);
        course.setCourseHasImage(true);
        course.setCourseIsActive(true);
        course.setLink("http://example.com/java");
        course.setArea(pt.uc.dei.enums.CourseArea.BACKEND);
        course.setLanguage(pt.uc.dei.enums.Language.ENGLISH);

        FinishedCourseEntity entity = new FinishedCourseEntity();
        entity.setId(100L);
        entity.setUser(user);
        entity.setCourse(course);
        entity.setCompletionDate(LocalDate.of(2024, 6, 1));

        FinishedCourseDTO dto = mapper.toDto(entity);
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(user.getId(), dto.getUserId());
        assertEquals(user.getEmail(), dto.getUserEmail());
        assertEquals(course.getId(), dto.getCourseId());
        assertEquals(course.getTitle(), dto.getCourseTitle());
        assertEquals(course.getDescription(), dto.getCourseDescription());
        assertEquals(entity.getCompletionDate(), dto.getCompletionDate());
        assertEquals(course.getArea().toString(), dto.getCourseArea());
        assertEquals(course.getDuration(), dto.getCourseDuration());
        assertEquals(course.getLanguage().getFieldName(), dto.getCourseLanguage());
        assertEquals(course.getLink(), dto.getCourseLink());
        assertEquals(course.getCourseHasImage(), dto.getCourseHasImage());
        assertEquals(course.getCourseIsActive(), dto.getCourseIsActive());
        assertEquals(course.getCreationDate(), dto.getCourseCreationDate());
    }

    @Test
    void testToDtoListAndSet() {
        FinishedCourseEntity entity1 = new FinishedCourseEntity();
        entity1.setId(1L);
        FinishedCourseEntity entity2 = new FinishedCourseEntity();
        entity2.setId(2L);
        List<FinishedCourseEntity> entityList = Arrays.asList(entity1, entity2);
        Set<FinishedCourseEntity> entitySet = new HashSet<>(entityList);
        List<FinishedCourseDTO> dtoList = mapper.toDtoList(entityList);
        Set<FinishedCourseDTO> dtoSet = mapper.toDtoSet(entitySet);
        assertEquals(2, dtoList.size());
        assertEquals(2, dtoSet.size());
    }

    @Test
    void testToEntity() {
        FinishedCourseDTO dto = new FinishedCourseDTO();
        dto.setId(200L);
        dto.setUserId(10L);
        dto.setUserEmail("test@user.com");
        dto.setCourseId(20L);
        dto.setCourseTitle("Python 101");
        dto.setCourseDescription("Intro to Python");
        dto.setCompletionDate(LocalDate.of(2024, 7, 1));
        dto.setCourseArea("frontend");
        dto.setCourseDuration(30);
        dto.setCourseLanguage("en");
        dto.setCourseLink("http://example.com/python");
        dto.setCourseHasImage(false);
        dto.setCourseIsActive(true);
        dto.setCourseCreationDate(LocalDate.of(2023, 2, 2));

        FinishedCourseEntity entity = mapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getCompletionDate(), entity.getCompletionDate());
        // user is ignored in mapping, so entity.getUser() should be null
        assertNull(entity.getUser());
    }

    @Test
    void testUpdateFinishedCourseFromDto() {
        FinishedCourseEntity entity = new FinishedCourseEntity();
        entity.setId(300L);
        entity.setCompletionDate(LocalDate.of(2022, 1, 1));
        // user and course are not updated by the mapper

        FinishedCourseDTO dto = new FinishedCourseDTO();
        dto.setCompletionDate(LocalDate.of(2025, 5, 5));
        // id should not be updated
        dto.setId(999L);

        mapper.updateFinishedCourseFromDto(dto, entity);
        assertEquals(LocalDate.of(2025, 5, 5), entity.getCompletionDate());
        assertEquals(300L, entity.getId());
    }

    @Test
    void testNullAndEmptyListMappings() {
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity((FinishedCourseDTO) null));
        assertTrue(mapper.toDtoList(null).isEmpty());
        assertTrue(mapper.toDtoSet(null).isEmpty());
        assertTrue(mapper.toDtoList(Collections.emptyList()).isEmpty());
        assertTrue(mapper.toDtoSet(Collections.emptySet()).isEmpty());
    }
}
