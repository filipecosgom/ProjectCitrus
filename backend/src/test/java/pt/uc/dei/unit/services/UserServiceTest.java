package pt.uc.dei.unit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.uc.dei.dtos.*;
import pt.uc.dei.entities.*;
import pt.uc.dei.enums.*;
import pt.uc.dei.mapper.FinishedCourseMapper;
import pt.uc.dei.mapper.UserMapper;
import pt.uc.dei.repositories.*;
import pt.uc.dei.services.EmailService;
import pt.uc.dei.services.NotificationService;
import pt.uc.dei.services.TokenService;
import pt.uc.dei.services.UserService;
import pt.uc.dei.utils.CSVGenerator;
import pt.uc.dei.utils.JWTUtil;
import pt.uc.dei.utils.TwoFactorUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock UserRepository userRepository;
    @Mock AppraisalRepository appraisalRepository;
    @Mock NotificationService notificationService;
    @Mock TemporaryUserRepository temporaryUserRepository;
    @Mock TokenService tokenService;
    @Mock CourseRepository courseRepository;
    @Mock FinishedCourseRepository finishedCourseRepository;
    @Mock EmailService emailService;
    @Mock JWTUtil jwtUtil;
    @Mock UserMapper userMapper;
    @Mock FinishedCourseMapper finishedCourseMapper;
    @Mock TwoFactorUtil twoFactorUtil;
    @Mock ActivationTokenRepository activationTokenRepository;

    @InjectMocks UserService userService;

    @BeforeEach
    void setUp() {
        // No-op, handled by @InjectMocks
    }

    @Test
    void testFindIfUserExists_foundInPermanent() {
        when(userRepository.findUserByEmail("test@example.com")).thenReturn(new UserEntity());
        when(temporaryUserRepository.findTemporaryUserByEmail(anyString())).thenReturn(null);
        assertTrue(userService.findIfUserExists("test@example.com"));
    }

    @Test
    void testFindIfUserExists_foundInTemporary() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(null);
        when(temporaryUserRepository.findTemporaryUserByEmail("temp@example.com")).thenReturn(new TemporaryUserEntity());
        assertTrue(userService.findIfUserExists("temp@example.com"));
    }

    @Test
    void testFindIfUserExists_notFound() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(null);
        when(temporaryUserRepository.findTemporaryUserByEmail(anyString())).thenReturn(null);
        assertFalse(userService.findIfUserExists("none@example.com"));
    }

    @Test
    void testUpdateUser_notFound() {
        when(userRepository.findUserById(1L)).thenReturn(null);
        UpdateUserDTO dto = new UpdateUserDTO();
        assertFalse(userService.updateUser(1L, dto));
    }

    @Test
    void testGetUser_found() {
        UserEntity entity = new UserEntity();
        UserDTO dto = new UserDTO();
        when(userRepository.findUserById(1L)).thenReturn(entity);
        when(userMapper.toDto(entity)).thenReturn(dto);
        assertEquals(dto, userService.getUser(1L));
    }

    @Test
    void testGetUserProfile_fullResponse() {
        UserEntity entity = new UserEntity();
        UserFullDTO dto = new UserFullDTO();
        when(userRepository.findUserById(1L)).thenReturn(entity);
        when(userMapper.toFullDto(entity)).thenReturn(dto);
        assertEquals(dto, userService.getUserProfile(1L, true));
    }

    @Test
    void testGetUserProfile_summaryResponse() {
        UserEntity entity = new UserEntity();
        UserResponseDTO dto = new UserResponseDTO();
        when(userRepository.findUserById(1L)).thenReturn(entity);
        when(userMapper.toUserResponseDto(entity)).thenReturn(dto);
        assertEquals(dto, userService.getUserProfile(1L, false));
    }

    @Test
    void testGenerateUsersCSV_success() {
        List<UserEntity> entities = Arrays.asList(new UserEntity(), new UserEntity());
        when(userRepository.getUsers(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), isNull(), isNull())).thenReturn(entities);
        when(userMapper.toDto(any(UserEntity.class))).thenReturn(new UserDTO());
        byte[] expected = new byte[]{1, 2, 3};
        mockStatic(CSVGenerator.class).when(() -> CSVGenerator.generateUserCSV(anyList(), any(), anyBoolean())).thenReturn(expected);
        byte[] result = userService.generateUsersCSV(null, null, null, null, null, null, null, null, null, null, Language.ENGLISH, null, null, true);
        assertArrayEquals(expected, result);
    }

    @Test
    void testGenerateUsersXLSX_success() {
        List<UserEntity> entities = Arrays.asList(new UserEntity(), new UserEntity());
        when(userRepository.getUsers(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), isNull(), isNull())).thenReturn(entities);
        when(userMapper.toDto(any(UserEntity.class))).thenReturn(new UserDTO());
        byte[] expected = new byte[]{4, 5, 6};
        mockStatic(pt.uc.dei.utils.XLSXGenerator.class).when(() -> pt.uc.dei.utils.XLSXGenerator.generateUserXLSX(anyList(), any(), anyBoolean())).thenReturn(expected);
        byte[] result = userService.generateUsersXLSX(null, null, null, null, null, null, null, null, null, null, Language.ENGLISH, null, null, true);
        assertArrayEquals(expected, result);
    }

    @Test
    void testDeleteTemporaryUserInformation_success() {
        TemporaryUserDTO dto = new TemporaryUserDTO();
        dto.setEmail("temp@user.com");
        TemporaryUserEntity entity = new TemporaryUserEntity();
        List<ActivationTokenEntity> tokens = Arrays.asList(new ActivationTokenEntity(), new ActivationTokenEntity());
        when(temporaryUserRepository.findTemporaryUserByEmail("temp@user.com")).thenReturn(entity);
        when(activationTokenRepository.getTokensOfUser(entity)).thenReturn(tokens);
        doNothing().when(activationTokenRepository).remove(any(ActivationTokenEntity.class));
        doNothing().when(activationTokenRepository).flush();
        // Can't stub private method, so just call and expect true (integration)
        // This will not test the private method directly
        assertTrue(userService.deleteTemporaryUserInformation(dto));
    }

    @Test
    void testCheckIfManagerOfUser_true() {
        UserEntity user = new UserEntity();
        UserEntity manager = new UserEntity();
        manager.setId(2L);
        user.setManagerUser(manager);
        when(userRepository.findUserById(1L)).thenReturn(user);
        assertTrue(userService.checkIfManagerOfUser(1L, 2L));
    }

    @Test
    void testCheckIfManagerOfUser_false() {
        UserEntity user = new UserEntity();
        user.setManagerUser(null);
        when(userRepository.findUserById(1L)).thenReturn(user);
        assertFalse(userService.checkIfManagerOfUser(1L, 2L));
    }

    @Test
    void testCheckIfUserIsAdmin_true() {
        UserEntity user = new UserEntity();
        user.setUserIsAdmin(true);
        when(userRepository.findUserById(1L)).thenReturn(user);
        assertTrue(userService.checkIfUserIsAdmin(1L));
    }

    @Test
    void testCheckIfUserIsAdmin_false() {
        UserEntity user = new UserEntity();
        user.setUserIsAdmin(false);
        when(userRepository.findUserById(1L)).thenReturn(user);
        assertFalse(userService.checkIfUserIsAdmin(1L));
    }

    @Test
    void testTemporaryUserEntityToTemporaryUserDTO() {
        TemporaryUserEntity entity = new TemporaryUserEntity();
        entity.setEmail("a@b.com");
        entity.setPassword("pw");
        entity.setId(1L);
        entity.setSecretKey("sk");
        TemporaryUserDTO dto = userService.temporaryUserEntityToTemporaryUserDTO(entity);
        assertEquals("a@b.com", dto.getEmail());
        assertEquals("pw", dto.getPassword());
        assertEquals(1L, dto.getId());
        assertEquals("sk", dto.getSecretKey());
    }

    @Test
    void testCheckAndUpdateAccountState_complete() {
        UserEntity user = mock(UserEntity.class);
        when(userRepository.findUserById(1L)).thenReturn(user);
        when(user.getHasAvatar()).thenReturn(true);
        when(user.getBiography()).thenReturn("bio");
        when(user.getBirthdate()).thenReturn(LocalDate.now());
        when(user.getMunicipality()).thenReturn("city");
        when(user.getName()).thenReturn("name");
        when(user.getPhone()).thenReturn("123");
        when(user.getPostalCode()).thenReturn("0000");
        when(user.getStreet()).thenReturn("street");
        when(user.getSurname()).thenReturn("surname");
        when(user.getAccountState()).thenReturn(AccountState.INCOMPLETE);
        doNothing().when(userRepository).persist(user);
        assertTrue(userService.checkAndUpdateAccountState(1L));
    }


    @Test
    void testUpdateAdminPermissions_success() {
        UserEntity requester = new UserEntity();
        requester.setUserIsAdmin(true);
        UserEntity user = new UserEntity();
        when(userRepository.findUserById(1L)).thenReturn(user);
        when(userRepository.findUserById(2L)).thenReturn(requester);
        // merge is void, so just doNothing
        doNothing().when(userRepository).merge(user);
        assertTrue(userService.updateAdminPermissions(1L, true, 2L));
    }

    @Test
    void testUpdateAdminPermissions_notAdmin() {
        UserEntity requester = new UserEntity();
        requester.setUserIsAdmin(false);
        when(userRepository.findUserById(2L)).thenReturn(requester);
        assertFalse(userService.updateAdminPermissions(1L, true, 2L));
    }

    @Test
    void testUpdateAdminPermissions_selfRemoval() {
        UserEntity requester = new UserEntity();
        requester.setUserIsAdmin(true);
        when(userRepository.findUserById(1L)).thenReturn(requester);
        assertFalse(userService.updateAdminPermissions(1L, false, 1L));
    }

    @Test
    void testUpdateAdminPermissions_userNotFound() {
        UserEntity requester = new UserEntity();
        requester.setUserIsAdmin(true);
        when(userRepository.findUserById(2L)).thenReturn(requester);
        when(userRepository.findUserById(1L)).thenReturn(null);
        assertFalse(userService.updateAdminPermissions(1L, true, 2L));
    }
}
