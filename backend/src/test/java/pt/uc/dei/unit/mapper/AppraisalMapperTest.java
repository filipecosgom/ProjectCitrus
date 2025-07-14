package pt.uc.dei.unit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pt.uc.dei.dtos.AppraisalDTO;
import pt.uc.dei.dtos.AppraisalResponseDTO;
import pt.uc.dei.entities.AppraisalEntity;
import pt.uc.dei.enums.AppraisalState;
import pt.uc.dei.mapper.AppraisalMapper;
import pt.uc.dei.mapper.AppraisalMapperImpl;
import pt.uc.dei.mapper.UserMapper;
import pt.uc.dei.dtos.UserResponseDTO;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppraisalMapperTest {
    private AppraisalMapper mapper;

    @BeforeEach
    void setUp() throws Exception {
        mapper = Mappers.getMapper(AppraisalMapper.class);
        // Inject mock UserMapper into AppraisalMapperImpl
        if (mapper instanceof AppraisalMapperImpl impl) {
            java.lang.reflect.Field userMapperField = AppraisalMapperImpl.class.getDeclaredField("userMapper");
            userMapperField.setAccessible(true);
            userMapperField.set(impl, new UserMapper() {
                @Override public pt.uc.dei.dtos.UserDTO toDto(pt.uc.dei.entities.UserEntity userEntity) { return null; }
                @Override public java.util.List<pt.uc.dei.dtos.UserDTO> toDtoList(java.util.List<pt.uc.dei.entities.UserEntity> users) { return java.util.Collections.emptyList(); }
                @Override public pt.uc.dei.entities.UserEntity toEntity(pt.uc.dei.dtos.UserDTO userDTO) { return null; }
                @Override public void updateUserFromDto(pt.uc.dei.dtos.UserDTO dto, pt.uc.dei.entities.UserEntity entity) {}
                @Override public pt.uc.dei.dtos.UserFullDTO toFullDto(pt.uc.dei.entities.UserEntity entity) { return null; }
                @Override public pt.uc.dei.dtos.UserResponseDTO toUserResponseDto(pt.uc.dei.entities.UserEntity entity) { return null; }
                @Override public pt.uc.dei.dtos.ManagerDTO toManagerDto(pt.uc.dei.entities.UserEntity managerUser) { return null; }
                @Override public pt.uc.dei.entities.UserEntity toManagerEntity(pt.uc.dei.dtos.ManagerDTO dto) { return null; }
            });
        }
    }

    @Test
    void testToDto() {
        AppraisalEntity entity = new AppraisalEntity();
        entity.setId(1L);
        entity.setFeedback("Great job");
        entity.setScore(4);
        entity.setState(AppraisalState.COMPLETED);
        entity.setCreationDate(LocalDate.of(2024, 1, 1));
        entity.setSubmissionDate(LocalDate.of(2024, 2, 1));

        // Mock users and cycle
        UserResponseDTO appraisedUser = new UserResponseDTO();
        appraisedUser.setId(10L);
        UserResponseDTO appraisingUser = new UserResponseDTO();
        appraisingUser.setId(20L);
        // Simulate mapping by setting users in entity (would be UserEntity in real case)
        // Here, just test that mapping does not throw and IDs are mapped
        // (MapStruct will use UserMapper for full mapping)
        // entity.setAppraisedUser(...)
        // entity.setAppraisingUser(...)
        // entity.setCycle(...)

        AppraisalDTO dto = mapper.toDto(entity);
        assertNotNull(dto);
        assertEquals(entity.getFeedback(), dto.getFeedback());
        assertEquals(entity.getScore(), dto.getScore());
        assertEquals(entity.getState(), dto.getState());
        assertEquals(entity.getCreationDate(), dto.getCreationDate());
        assertEquals(entity.getSubmissionDate(), dto.getSubmissionDate());
    }

    @Test
    void testToEntity() {
        AppraisalDTO dto = new AppraisalDTO();
        dto.setFeedback("Needs improvement");
        dto.setScore(2);
        dto.setState(AppraisalState.IN_PROGRESS);
        dto.setCreationDate(LocalDate.of(2025, 3, 15));
        dto.setSubmissionDate(LocalDate.of(2025, 4, 1));

        AppraisalEntity entity = mapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals(dto.getFeedback(), entity.getFeedback());
        assertEquals(dto.getScore(), entity.getScore());
        assertEquals(dto.getState(), entity.getState());
        assertEquals(dto.getCreationDate(), entity.getCreationDate());
        assertEquals(dto.getSubmissionDate(), entity.getSubmissionDate());
    }

    @Test
    void testToDtoList() {
        AppraisalEntity entity1 = new AppraisalEntity();
        entity1.setFeedback("A");
        AppraisalEntity entity2 = new AppraisalEntity();
        entity2.setFeedback("B");
        List<AppraisalEntity> entities = Arrays.asList(entity1, entity2);
        List<AppraisalDTO> dtos = mapper.toDtoList(entities);
        assertEquals(2, dtos.size());
        assertEquals("A", dtos.get(0).getFeedback());
        assertEquals("B", dtos.get(1).getFeedback());
    }

    @Test
    void testToEntityList() {
        AppraisalDTO dto1 = new AppraisalDTO();
        dto1.setFeedback("X");
        AppraisalDTO dto2 = new AppraisalDTO();
        dto2.setFeedback("Y");
        List<AppraisalDTO> dtos = Arrays.asList(dto1, dto2);
        List<AppraisalEntity> entities = mapper.toEntityList(dtos);
        assertEquals(2, entities.size());
        assertEquals("X", entities.get(0).getFeedback());
        assertEquals("Y", entities.get(1).getFeedback());
    }

    @Test
    void testUpdateEntityFromDto() {
        AppraisalEntity entity = new AppraisalEntity();
        entity.setFeedback("Old");
        entity.setScore(1);
        entity.setState(AppraisalState.IN_PROGRESS);
        entity.setCreationDate(LocalDate.of(2023, 1, 1));

        AppraisalDTO dto = new AppraisalDTO();
        dto.setFeedback("Updated");
        dto.setScore(3);
        dto.setState(AppraisalState.COMPLETED);

        mapper.updateEntityFromDto(dto, entity);
        assertEquals("Updated", entity.getFeedback());
        assertEquals(3, entity.getScore());
        assertEquals(AppraisalState.COMPLETED, entity.getState());
        // creationDate and other ignored fields should remain unchanged
        assertEquals(LocalDate.of(2023, 1, 1), entity.getCreationDate());
    }

    @Test
    void testNullAndEmptyListMappings() {
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity(null));
        assertTrue(mapper.toDtoList(null).isEmpty());
        assertTrue(mapper.toEntityList(null).isEmpty());
        assertTrue(mapper.toDtoList(Collections.emptyList()).isEmpty());
        assertTrue(mapper.toEntityList(Collections.emptyList()).isEmpty());
    }
}
