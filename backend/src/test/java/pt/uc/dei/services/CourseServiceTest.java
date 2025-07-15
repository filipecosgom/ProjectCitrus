package pt.uc.dei.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.uc.dei.dtos.CourseDTO;
import pt.uc.dei.dtos.CourseNewDTO;
import pt.uc.dei.dtos.CourseUpdateDTO;
import pt.uc.dei.entities.CourseEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.CourseArea;
import pt.uc.dei.enums.Language;
import pt.uc.dei.enums.OrderBy;
import pt.uc.dei.mapper.CourseMapper;
import pt.uc.dei.repositories.CourseRepository;
import pt.uc.dei.repositories.UserRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {
    @Mock CourseRepository courseRepository;
    @Mock CourseMapper courseMapper;
    @Mock UserRepository userRepository;
    @InjectMocks CourseService courseService;

    private CourseEntity courseEntity;
    private CourseDTO courseDTO;
    private CourseNewDTO courseNewDTO;
    private CourseUpdateDTO courseUpdateDTO;
    private UserEntity adminUser;

    @BeforeEach
    void setUp() {
        courseEntity = new CourseEntity();
        courseEntity.setId(1L);
        courseEntity.setTitle("Test Course");
        courseEntity.setLink("http://test.com");
        courseEntity.setCreationDate(LocalDate.now());
        adminUser = new UserEntity();
        adminUser.setId(2L);
        courseEntity.setAdmin(adminUser);

        courseDTO = new CourseDTO();
        courseDTO.setId(1L);
        courseDTO.setTitle("Test Course");
        courseDTO.setLink("http://test.com");

        courseNewDTO = new CourseNewDTO();
        courseNewDTO.setTitle("Test Course");
        courseNewDTO.setLink("http://test.com");

        courseUpdateDTO = new CourseUpdateDTO();
        courseUpdateDTO.setId(1L);
        courseUpdateDTO.setTitle("Test Course");
        courseUpdateDTO.setLink("http://test.com");
    }

    @Nested
    @DisplayName("createNewCourse")
    class CreateNewCourse {
        @Test
        void returnsCourseDTOOnSuccess() {
            when(courseRepository.existsByTitle("Test Course")).thenReturn(false);
            when(courseRepository.existsByLink("http://test.com")).thenReturn(false);
            when(userRepository.findUserById(2L)).thenReturn(adminUser);
            when(courseMapper.toEntity(courseNewDTO)).thenReturn(courseEntity);
            when(courseMapper.toDto(courseEntity)).thenReturn(courseDTO);
            doNothing().when(courseRepository).persist(courseEntity);
            CourseDTO result = courseService.createNewCourse(courseNewDTO, 2L);
            assertNotNull(result);
            assertEquals(courseDTO, result);
        }
        @Test
        void returnsNullIfDtoOrTitleOrAdminIdNull() {
            assertNull(courseService.createNewCourse(null, 2L));
            assertNull(courseService.createNewCourse(courseNewDTO, null));
            courseNewDTO.setTitle(null);
            assertNull(courseService.createNewCourse(courseNewDTO, 2L));
        }
        @Test
        void throwsExceptionIfDuplicateTitle() {
            when(courseRepository.existsByTitle("Test Course")).thenReturn(true);
            assertThrows(IllegalArgumentException.class, () -> courseService.createNewCourse(courseNewDTO, 2L));
        }
        @Test
        void throwsExceptionIfDuplicateLink() {
            when(courseRepository.existsByTitle("Test Course")).thenReturn(false);
            when(courseRepository.existsByLink("http://test.com")).thenReturn(true);
            assertThrows(IllegalArgumentException.class, () -> courseService.createNewCourse(courseNewDTO, 2L));
        }
        @Test
        void returnsNullIfAdminNotFound() {
            when(courseRepository.existsByTitle("Test Course")).thenReturn(false);
            when(courseRepository.existsByLink("http://test.com")).thenReturn(false);
            when(userRepository.findUserById(2L)).thenReturn(null);
            assertNull(courseService.createNewCourse(courseNewDTO, 2L));
        }
    }

    @Nested
    @DisplayName("updateCourse")
    class UpdateCourse {
        @Test
        void returnsTrueOnSuccess() {
            when(courseRepository.findCourseById(1L)).thenReturn(courseEntity);
            when(courseRepository.existsByTitle("Test Course")).thenReturn(false);
            when(courseRepository.existsByLink("http://test.com")).thenReturn(false);
            doNothing().when(courseMapper).updateEntityFromUpdateDto(courseUpdateDTO, courseEntity);
            doNothing().when(courseRepository).persist(courseEntity);
            assertTrue(courseService.updateCourse(courseUpdateDTO));
        }
        @Test
        void returnsFalseIfDtoOrIdNull() {
            assertFalse(courseService.updateCourse(null));
            courseUpdateDTO.setId(null);
            assertFalse(courseService.updateCourse(courseUpdateDTO));
        }
        @Test
        void returnsFalseIfCourseNotFound() {
            when(courseRepository.findCourseById(1L)).thenReturn(null);
            assertFalse(courseService.updateCourse(courseUpdateDTO));
        }
        @Test
        void throwsExceptionIfDuplicateTitle() {
            when(courseRepository.findCourseById(1L)).thenReturn(courseEntity);
            when(courseRepository.existsByTitle("Test Course")).thenReturn(true);
            assertThrows(IllegalArgumentException.class, () -> courseService.updateCourse(courseUpdateDTO));
        }
        @Test
        void throwsExceptionIfDuplicateLink() {
            when(courseRepository.findCourseById(1L)).thenReturn(courseEntity);
            when(courseRepository.existsByTitle("Test Course")).thenReturn(false);
            when(courseRepository.existsByLink("http://test.com")).thenReturn(true);
            assertThrows(IllegalArgumentException.class, () -> courseService.updateCourse(courseUpdateDTO));
        }
    }

    @Nested
    @DisplayName("countAllCourses/countCoursesByActive")
    class CountCourses {
        @Test
        void countAllCoursesReturnsValue() {
            when(courseRepository.countCoursesWithFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(42L);
            assertEquals(42L, courseService.countAllCourses());
        }
        @Test
        void countCoursesByActiveReturnsValue() {
            when(courseRepository.countCoursesWithFilters(any(), any(), any(), any(), any(), any(), any(), eq(true), any(), any())).thenReturn(10L);
            assertEquals(10L, courseService.countCoursesByActive(true));
        }
    }

    @Nested
    @DisplayName("getCoursesWithFilters")
    class GetCoursesWithFilters {
        @Test
        void returnsMapWithCoursesAndPagination() {
            List<CourseEntity> courseEntities = List.of(courseEntity);
            when(courseRepository.findCoursesWithFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(courseEntities);
            when(courseRepository.countCoursesWithFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(1L);
            when(courseMapper.toDto(courseEntity)).thenReturn(courseDTO);
            Map<String, Object> result = courseService.getCoursesWithFilters(null, null, null, null, null, null, null, null, null, null, 0, 10, null, null);
            assertNotNull(result);
            assertTrue(result.containsKey("courses"));
            assertTrue(result.containsKey("totalCourses"));
            assertTrue(result.containsKey("offset"));
            assertTrue(result.containsKey("limit"));
            List<CourseDTO> dtos = (List<CourseDTO>) result.get("courses");
            assertEquals(1, dtos.size());
            assertEquals(courseDTO, dtos.get(0));
            assertEquals(1L, result.get("totalCourses"));
            assertEquals(0, result.get("offset"));
            assertEquals(10, result.get("limit"));
        }
        @Test
        void returnsEmptyListIfNoCoursesFound() {
            when(courseRepository.findCoursesWithFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(Collections.emptyList());
            when(courseRepository.countCoursesWithFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(0L);
            Map<String, Object> result = courseService.getCoursesWithFilters(null, null, null, null, null, null, null, null, null, null, 0, 10, null, null);
            assertNotNull(result);
            List<CourseDTO> dtos = (List<CourseDTO>) result.get("courses");
            assertTrue(dtos.isEmpty());
            assertEquals(0L, result.get("totalCourses"));
        }
    }
}
