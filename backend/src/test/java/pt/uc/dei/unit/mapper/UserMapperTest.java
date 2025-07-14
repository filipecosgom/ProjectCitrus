package pt.uc.dei.unit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pt.uc.dei.dtos.*;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.enums.Office;
import pt.uc.dei.enums.Role;
import pt.uc.dei.mapper.AppraisalMapper;
import pt.uc.dei.mapper.FinishedCourseMapper;
import pt.uc.dei.mapper.UserMapper;
import pt.uc.dei.mapper.UserMapperImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UserMapper}.
 * Covers entity-to-DTO, DTO-to-entity, update, manager mapping, null/empty, and list/set scenarios.
 */
class UserMapperTest {
    private UserMapper userMapper;

    @BeforeEach
    void setUp() throws Exception {
        userMapper = Mappers.getMapper(UserMapper.class);
        // Use reflection to inject mock mappers into private fields
        if (userMapper instanceof UserMapperImpl impl) {
            java.lang.reflect.Field appraisalField = UserMapperImpl.class.getDeclaredField("appraisalMapper");
            appraisalField.setAccessible(true);
            appraisalField.set(impl, new AppraisalMapper() {
                @Override public AppraisalDTO toDto(pt.uc.dei.entities.AppraisalEntity appraisalEntity) { return null; }
                @Override public AppraisalResponseDTO toResponseDto(pt.uc.dei.entities.AppraisalEntity appraisalEntity) { return null; }
                @Override public pt.uc.dei.entities.AppraisalEntity toEntity(AppraisalDTO appraisalDTO) { return null; }
                @Override public List<AppraisalDTO> toDtoList(List<pt.uc.dei.entities.AppraisalEntity> entities) { return Collections.emptyList(); }
                @Override public List<pt.uc.dei.entities.AppraisalEntity> toEntityList(List<AppraisalDTO> dtos) { return Collections.emptyList(); }
                @Override public void updateEntityFromDto(AppraisalDTO dto, pt.uc.dei.entities.AppraisalEntity entity) {}
            });
            java.lang.reflect.Field finishedCourseField = UserMapperImpl.class.getDeclaredField("finishedCourseMapper");
            finishedCourseField.setAccessible(true);
            finishedCourseField.set(impl, new FinishedCourseMapper() {
                @Override public pt.uc.dei.dtos.FinishedCourseDTO toDto(pt.uc.dei.entities.FinishedCourseEntity entity) { return null; }
                @Override public java.util.List<pt.uc.dei.dtos.FinishedCourseDTO> toDtoList(java.util.List<pt.uc.dei.entities.FinishedCourseEntity> entities) { return Collections.emptyList(); }
                @Override public java.util.Set<pt.uc.dei.dtos.FinishedCourseDTO> toDtoSet(java.util.Set<pt.uc.dei.entities.FinishedCourseEntity> entities) { return Collections.emptySet(); }
                @Override public pt.uc.dei.entities.FinishedCourseEntity toEntity(pt.uc.dei.dtos.FinishedCourseDTO dto) { return null; }
                @Override public void updateFinishedCourseFromDto(pt.uc.dei.dtos.FinishedCourseDTO dto, pt.uc.dei.entities.FinishedCourseEntity entity) {}
            });
        }
    }

    @Test
    void testToDto_basicFields() {
        UserEntity entity = buildUserEntity();
        UserDTO dto = userMapper.toDto(entity);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getEmail(), dto.getEmail());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getSurname(), dto.getSurname());
        assertEquals(entity.getUserIsAdmin(), dto.getUserIsAdmin());
        assertEquals(entity.getUserIsManager(), dto.getUserIsManager());
        assertEquals(entity.getOffice(), dto.getOffice());
        assertEquals(entity.getPhone(), dto.getPhone());
        assertEquals(entity.getBirthdate(), dto.getBirthdate());
        assertEquals(entity.getStreet(), dto.getStreet());
        assertEquals(entity.getPostalCode(), dto.getPostalCode());
        assertEquals(entity.getMunicipality(), dto.getMunicipality());
        assertEquals(entity.getBiography(), dto.getBiography());
        assertEquals(entity.getAccountState(), dto.getAccountState());
        assertEquals(entity.getRole(), dto.getRole());
        assertEquals(entity.getCreationDate(), dto.getCreationDate());
        assertNull(dto.getPassword()); // password is ignored
        assertNotNull(dto.getManager());
        assertEquals(entity.getManagerUser().getId(), dto.getManager().getId());
    }

    @Test
    void testToDto_nullManager() {
        UserEntity entity = buildUserEntity();
        entity.setManagerUser(null);
        UserDTO dto = userMapper.toDto(entity);
        assertNull(dto.getManager());
    }

    @Test
    void testToEntity_basicFields() {
        UserDTO dto = buildUserDTO();
        UserEntity entity = userMapper.toEntity(dto);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getEmail(), entity.getEmail());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getSurname(), entity.getSurname());
        assertEquals(dto.getUserIsAdmin(), entity.getUserIsAdmin());
        assertEquals(dto.getUserIsManager(), entity.getUserIsManager());
        assertEquals(dto.getOffice(), entity.getOffice());
        assertEquals(dto.getPhone(), entity.getPhone());
        assertEquals(dto.getBirthdate(), entity.getBirthdate());
        assertEquals(dto.getStreet(), entity.getStreet());
        assertEquals(dto.getPostalCode(), entity.getPostalCode());
        assertEquals(dto.getMunicipality(), entity.getMunicipality());
        assertEquals(dto.getBiography(), entity.getBiography());
        assertEquals(dto.getAccountState(), entity.getAccountState());
        assertEquals(dto.getRole(), entity.getRole());
        assertNull(entity.getPassword()); // password is ignored
        assertNotNull(entity.getManagerUser());
        assertEquals(dto.getManager().getId(), entity.getManagerUser().getId());
    }

    @Test
    void testToEntity_nullManager() {
        UserDTO dto = buildUserDTO();
        dto.setManager(null);
        UserEntity entity = userMapper.toEntity(dto);
        assertNull(entity.getManagerUser());
    }

    @Test
    void testToFullDto() {
        UserEntity entity = buildUserEntity();
        UserFullDTO dto = userMapper.toFullDto(entity);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getEmail(), dto.getEmail());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getSurname(), dto.getSurname());
        assertEquals(entity.getUserIsAdmin(), dto.getUserIsAdmin());
        assertEquals(entity.getUserIsManager(), dto.getUserIsManager());
        assertEquals(entity.getOffice(), dto.getOffice());
        assertEquals(entity.getPhone(), dto.getPhone());
        assertEquals(entity.getBirthdate(), dto.getBirthdate());
        assertEquals(entity.getStreet(), dto.getStreet());
        assertEquals(entity.getPostalCode(), dto.getPostalCode());
        assertEquals(entity.getMunicipality(), dto.getMunicipality());
        assertEquals(entity.getBiography(), dto.getBiography());
        assertEquals(entity.getAccountState(), dto.getAccountState());
        assertEquals(entity.getRole(), dto.getRole());
        assertNull(dto.getPassword());
        assertNotNull(dto.getManager());
        // Nested lists/sets are mapped (AppraisalDTO, FinishedCourseDTO)
    }

    @Test
    void testToUserResponseDto() {
        UserEntity entity = buildUserEntity();
        entity.setOnlineStatus(true);
        entity.setLastSeen(LocalDateTime.now());
        UserResponseDTO dto = userMapper.toUserResponseDto(entity);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getEmail(), dto.getEmail());
        assertEquals(entity.getUserIsAdmin(), dto.getUserIsAdmin());
        assertEquals(entity.getUserIsManager(), dto.getUserIsManager());
        assertEquals(entity.getAccountState(), dto.getAccountState());
        assertEquals(entity.getRole(), dto.getRole());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getSurname(), dto.getSurname());
        assertEquals(entity.getHasAvatar(), dto.getHasAvatar());
        assertEquals(entity.getLastSeen(), dto.getLastSeen());
        assertEquals(entity.getOnlineStatus(), dto.isOnlineStatus());
        assertNull(dto.getPassword());
        assertNotNull(dto.getManager());
    }

    @Test
    void testToManagerDto() {
        UserEntity manager = buildManagerEntity();
        ManagerDTO dto = userMapper.toManagerDto(manager);
        assertEquals(manager.getId(), dto.getId());
        assertEquals(manager.getName(), dto.getName());
        assertEquals(manager.getSurname(), dto.getSurname());
        assertEquals(manager.getRole(), dto.getRole());
        assertEquals(manager.getHasAvatar(), dto.getHasAvatar());
        assertEquals(manager.getEmail(), dto.getEmail());
    }

    @Test
    void testToManagerEntity() {
        ManagerDTO dto = new ManagerDTO(2L, "Jane", "Doe", Role.CTO, true, "jane@company.com");
        UserEntity entity = userMapper.toManagerEntity(dto);
        assertEquals(dto.getId(), entity.getId());
    }

    @Test
    void testToDtoList() {
        List<UserEntity> entities = Arrays.asList(buildUserEntity(), buildUserEntity());
        List<UserDTO> dtos = userMapper.toDtoList(entities);
        assertEquals(2, dtos.size());
    }

    @Test
    void testUpdateUserFromDto() {
        UserEntity entity = buildUserEntity();
        UserDTO dto = buildUserDTO();
        dto.setName("Updated");
        dto.setSurname("User");
        userMapper.updateUserFromDto(dto, entity);
        assertEquals("Updated", entity.getName());
        assertEquals("User", entity.getSurname());
    }

    @Test
    void testNullAndEmptyCases() {
        assertNull(userMapper.toDto(null));
        assertNull(userMapper.toEntity(null));
        assertNull(userMapper.toFullDto(null));
        assertNull(userMapper.toUserResponseDto(null));
        assertNull(userMapper.toManagerDto(null));
        assertNull(userMapper.toManagerEntity(null));
        assertTrue(userMapper.toDtoList(Collections.emptyList()).isEmpty());
    }

    // --- Helper methods to build test data ---
    private UserEntity buildUserEntity() {
        UserEntity manager = buildManagerEntity();
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setEmail("user@company.com");
        entity.setPassword("secret");
        entity.setHasAvatar(true);
        entity.setName("John");
        entity.setSurname("Doe");
        entity.setUserIsAdmin(false);
        entity.setUserIsDeleted(false);
        entity.setUserIsManager(false);
        entity.setOffice(Office.COIMBRA);
        entity.setPhone("123456789");
        entity.setBirthdate(LocalDate.of(1990, 1, 1));
        entity.setStreet("Main St");
        entity.setPostalCode("1234-567");
        entity.setMunicipality("Coimbra");
        entity.setBiography("A test user");
        entity.setAccountState(AccountState.COMPLETE);
        entity.setRole(Role.SOFTWARE_ENGINEER);
        entity.setCreationDate(LocalDateTime.now());
        entity.setManagerUser(manager);
        entity.setOnlineStatus(false);
        entity.setLastSeen(LocalDateTime.now());
        return entity;
    }

    private UserEntity buildManagerEntity() {
        UserEntity manager = new UserEntity();
        manager.setId(2L);
        manager.setName("Jane");
        manager.setSurname("Doe");
        manager.setRole(Role.CTO);
        manager.setHasAvatar(true);
        manager.setEmail("jane@company.com");
        return manager;
    }

    private UserDTO buildUserDTO() {
        ManagerDTO manager = new ManagerDTO(2L, "Jane", "Doe", Role.CTO, true, "jane@company.com");
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setEmail("user@company.com");
        dto.setPassword("secret");
        dto.setHasAvatar(true);
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setUserIsAdmin(false);
        dto.setUserIsDeleted(false);
        dto.setUserIsManager(false);
        dto.setOffice(Office.COIMBRA);
        dto.setPhone("123456789");
        dto.setBirthdate(LocalDate.of(1990, 1, 1));
        dto.setStreet("Main St");
        dto.setPostalCode("1234-567");
        dto.setMunicipality("Coimbra");
        dto.setBiography("A test user");
        dto.setAccountState(AccountState.COMPLETE);
        dto.setRole(Role.SOFTWARE_ENGINEER);
        dto.setCreationDate(LocalDateTime.now());
        dto.setManager(manager);
        return dto;
    }
}
