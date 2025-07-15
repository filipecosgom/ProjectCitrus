package pt.uc.dei.unit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.uc.dei.dtos.CycleDTO;
import pt.uc.dei.dtos.CycleUpdateDTO;
import pt.uc.dei.entities.AppraisalEntity;
import pt.uc.dei.entities.CycleEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.AppraisalState;
import pt.uc.dei.enums.CycleState;
import pt.uc.dei.mapper.CycleMapper;
import pt.uc.dei.repositories.AppraisalRepository;
import pt.uc.dei.repositories.CycleRepository;
import pt.uc.dei.repositories.UserRepository;
import pt.uc.dei.services.CycleService;
import pt.uc.dei.services.EmailService;
import pt.uc.dei.services.NotificationService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CycleServiceTest {
    @Mock CycleRepository cycleRepository;
    @Mock UserRepository userRepository;
    @Mock AppraisalRepository appraisalRepository;
    @Mock EmailService emailService;
    @Mock CycleMapper cycleMapper;
    @Mock NotificationService notificationService;
    @InjectMocks CycleService cycleService;

    private CycleEntity cycleEntity;
    private CycleDTO cycleDTO;
    private UserEntity adminUser;
    private CycleUpdateDTO cycleUpdateDTO;
    private AppraisalEntity appraisalEntity;

    @BeforeEach
    void setUp() {
        cycleEntity = new CycleEntity();
        cycleEntity.setId(1L);
        cycleEntity.setStartDate(LocalDate.now().plusDays(1));
        cycleEntity.setEndDate(LocalDate.now().plusDays(10));
        cycleEntity.setState(CycleState.OPEN);
        adminUser = new UserEntity();
        adminUser.setId(2L);
        adminUser.setName("Admin");
        adminUser.setSurname("User");
        cycleEntity.setAdmin(adminUser);

        cycleDTO = new CycleDTO();
        cycleDTO.setId(1L);
        cycleDTO.setStartDate(LocalDate.now().plusDays(1));
        cycleDTO.setEndDate(LocalDate.now().plusDays(10));
        cycleDTO.setAdminId(2L);

        cycleUpdateDTO = new CycleUpdateDTO();
        cycleUpdateDTO.setId(1L);
        cycleUpdateDTO.setStartDate(LocalDate.now().plusDays(2));
        cycleUpdateDTO.setEndDate(LocalDate.now().plusDays(11));
        cycleUpdateDTO.setAdminId(2L);
        cycleUpdateDTO.setState(CycleState.OPEN);

        appraisalEntity = new AppraisalEntity();
        appraisalEntity.setState(AppraisalState.IN_PROGRESS);
    }

    @Nested
    @DisplayName("createCycle")
    class CreateCycle {
        @Test
        void createsCycleSuccessfully() {
            when(userRepository.findActiveUsersWithoutManager()).thenReturn(Collections.emptyList());
            when(userRepository.find(2L)).thenReturn(adminUser);
            when(cycleRepository.hasOverlappingCycles(any(), any(), isNull())).thenReturn(false);
            when(cycleMapper.toEntity(any(CycleDTO.class))).thenReturn(cycleEntity);
            doNothing().when(cycleRepository).persist(any(CycleEntity.class));
            when(userRepository.findManagersAndAdmins()).thenReturn(Collections.singletonList(adminUser));
            doNothing().when(notificationService).newCycleOpenNotification(any(), any());
            when(cycleMapper.toDto(any(CycleEntity.class))).thenReturn(cycleDTO);
            CycleDTO result = cycleService.createCycle(cycleDTO);
            assertNotNull(result);
            assertEquals(cycleDTO, result);
        }
        @Test
        void throwsIfUsersWithoutManager() {
            UserEntity user = new UserEntity();
            user.setEmail("no.manager@example.com");
            when(userRepository.findActiveUsersWithoutManager()).thenReturn(List.of(user));
            Exception ex = assertThrows(IllegalStateException.class, () -> cycleService.createCycle(cycleDTO));
            assertTrue(ex.getMessage().contains("without manager"));
        }
        @Test
        void throwsIfAdminNotFound() {
            when(userRepository.findActiveUsersWithoutManager()).thenReturn(Collections.emptyList());
            when(userRepository.find(2L)).thenReturn(null);
            Exception ex = assertThrows(IllegalArgumentException.class, () -> cycleService.createCycle(cycleDTO));
            assertTrue(ex.getMessage().contains("Admin user not found"));
        }
        @Test
        void throwsIfStartDateAfterEndDate() {
            cycleDTO.setStartDate(LocalDate.now().plusDays(10));
            cycleDTO.setEndDate(LocalDate.now().plusDays(1));
            when(userRepository.findActiveUsersWithoutManager()).thenReturn(Collections.emptyList());
            when(userRepository.find(2L)).thenReturn(adminUser);
            Exception ex = assertThrows(IllegalArgumentException.class, () -> cycleService.createCycle(cycleDTO));
            assertTrue(ex.getMessage().contains("Start date must be before end date"));
        }
        @Test
        void throwsIfStartDateInPast() {
            cycleDTO.setStartDate(LocalDate.now().minusDays(1));
            cycleDTO.setEndDate(LocalDate.now().plusDays(1));
            when(userRepository.findActiveUsersWithoutManager()).thenReturn(Collections.emptyList());
            when(userRepository.find(2L)).thenReturn(adminUser);
            Exception ex = assertThrows(IllegalArgumentException.class, () -> cycleService.createCycle(cycleDTO));
            assertTrue(ex.getMessage().contains("Start date cannot be in the past"));
        }
        @Test
        void throwsIfOverlappingCycles() {
            when(userRepository.findActiveUsersWithoutManager()).thenReturn(Collections.emptyList());
            when(userRepository.find(2L)).thenReturn(adminUser);
            when(cycleRepository.hasOverlappingCycles(any(), any(), isNull())).thenReturn(true);
            Exception ex = assertThrows(IllegalStateException.class, () -> cycleService.createCycle(cycleDTO));
            assertTrue(ex.getMessage().contains("already exists"));
        }
    }

    @Nested
    @DisplayName("closeCycle")
    class CloseCycle {
        @Test
        void closesCycleSuccessfully() {
            when(cycleRepository.find(1L)).thenReturn(cycleEntity);
            when(userRepository.findManagersAndAdmins()).thenReturn(Collections.singletonList(adminUser));
            doNothing().when(notificationService).newCycleCloseNotification(any(), any());
            doNothing().when(cycleRepository).merge(any(CycleEntity.class));
            when(cycleMapper.toDto(any(CycleEntity.class))).thenReturn(cycleDTO);
            // Use real canCloseCycle for this test
            CycleDTO result = cycleService.closeCycle(1L);
            assertNotNull(result);
        }
        @Test
        void throwsIfCannotClose() {
            Map<String, Object> validation = new HashMap<>();
            validation.put("canClose", false);
            validation.put("reason", "Not ready");
            CycleService spy = Mockito.spy(cycleService);
            Mockito.doReturn(validation).when(spy).canCloseCycle(1L);
            Exception ex = assertThrows(IllegalStateException.class, () -> spy.closeCycle(1L));
            assertTrue(ex.getMessage().contains("Not ready"));
        }
    }

    @Nested
    @DisplayName("updateCycle")
    class UpdateCycle {
        @Test
        void updatesCycleSuccessfully() {
            when(cycleRepository.find(1L)).thenReturn(cycleEntity);
            when(cycleRepository.hasOverlappingCycles(any(), any(), eq(1L))).thenReturn(false);
            doNothing().when(cycleMapper).updateEntityFromDto(any(CycleUpdateDTO.class), any(CycleEntity.class));
            doNothing().when(cycleRepository).merge(any(CycleEntity.class));
            when(cycleMapper.toDto(any(CycleEntity.class))).thenReturn(cycleDTO);
            CycleDTO result = cycleService.updateCycle(cycleUpdateDTO);
            assertNotNull(result);
        }
        @Test
        void throwsIfCycleNotFound() {
            when(cycleRepository.find(1L)).thenReturn(null);
            Exception ex = assertThrows(IllegalArgumentException.class, () -> cycleService.updateCycle(cycleUpdateDTO));
            assertTrue(ex.getMessage().contains("Cycle not found"));
        }
        @Test
        void throwsIfClosed() {
            cycleEntity.setState(CycleState.CLOSED);
            when(cycleRepository.find(1L)).thenReturn(cycleEntity);
            Exception ex = assertThrows(IllegalStateException.class, () -> cycleService.updateCycle(cycleUpdateDTO));
            assertTrue(ex.getMessage().contains("Cannot modify a closed cycle"));
        }
        @Test
        void throwsIfStartDateAfterEndDate() {
            when(cycleRepository.find(1L)).thenReturn(cycleEntity);
            cycleUpdateDTO.setStartDate(LocalDate.now().plusDays(10));
            cycleUpdateDTO.setEndDate(LocalDate.now().plusDays(1));
            Exception ex = assertThrows(IllegalArgumentException.class, () -> cycleService.updateCycle(cycleUpdateDTO));
            assertTrue(ex.getMessage().contains("Start date must be before end date"));
        }
        @Test
        void throwsIfOverlappingCycles() {
            when(cycleRepository.find(1L)).thenReturn(cycleEntity);
            when(cycleRepository.hasOverlappingCycles(any(), any(), eq(1L))).thenReturn(true);
            Exception ex = assertThrows(IllegalStateException.class, () -> cycleService.updateCycle(cycleUpdateDTO));
            assertTrue(ex.getMessage().contains("Another cycle already exists"));
        }
    }

    @Nested
    @DisplayName("canCloseCycle")
    class CanCloseCycle {
        @Test
        void returnsTrueIfAllAppraisalsCompletedOrClosed() {
            when(cycleRepository.find(1L)).thenReturn(cycleEntity);
            AppraisalEntity completed = new AppraisalEntity();
            completed.setState(AppraisalState.COMPLETED);
            AppraisalEntity closed = new AppraisalEntity();
            closed.setState(AppraisalState.CLOSED);
            when(appraisalRepository.findAppraisalsByCycle(1L)).thenReturn(List.of(completed, closed));
            Map<String, Object> result = cycleService.canCloseCycle(1L);
            assertTrue((Boolean) result.get("canClose"));
        }
        @Test
        void returnsFalseIfAnyInProgress() {
            when(cycleRepository.find(1L)).thenReturn(cycleEntity);
            AppraisalEntity inProgress = new AppraisalEntity();
            inProgress.setState(AppraisalState.IN_PROGRESS);
            AppraisalEntity completed = new AppraisalEntity();
            completed.setState(AppraisalState.COMPLETED);
            when(appraisalRepository.findAppraisalsByCycle(1L)).thenReturn(List.of(inProgress, completed));
            Map<String, Object> result = cycleService.canCloseCycle(1L);
            assertFalse((Boolean) result.get("canClose"));
        }
        @Test
        void throwsIfCycleNotFound() {
            when(cycleRepository.find(1L)).thenReturn(null);
            assertThrows(IllegalArgumentException.class, () -> cycleService.canCloseCycle(1L));
        }
        @Test
        void throwsIfCycleAlreadyClosed() {
            cycleEntity.setState(CycleState.CLOSED);
            when(cycleRepository.find(1L)).thenReturn(cycleEntity);
            assertThrows(IllegalStateException.class, () -> cycleService.canCloseCycle(1L));
        }
    }
}
