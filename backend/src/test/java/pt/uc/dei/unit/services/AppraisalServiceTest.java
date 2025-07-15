package pt.uc.dei.unit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.uc.dei.dtos.*;
import pt.uc.dei.entities.*;
import pt.uc.dei.enums.*;
import pt.uc.dei.mapper.AppraisalMapper;
import pt.uc.dei.repositories.AppraisalRepository;
import pt.uc.dei.repositories.CycleRepository;
import pt.uc.dei.repositories.UserRepository;
import pt.uc.dei.services.AppraisalService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppraisalServiceTest {
    @Mock
    private AppraisalRepository appraisalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CycleRepository cycleRepository;
    @Mock
    private AppraisalMapper appraisalMapper;

    @InjectMocks
    private AppraisalService appraisalService;

    private UserEntity appraisedUser;
    private UserEntity appraisingUser;
    private CycleEntity cycle;
    private AppraisalEntity appraisalEntity;
    private CreateAppraisalDTO createAppraisalDTO;
    private UpdateAppraisalDTO updateAppraisalDTO;
    private AppraisalDTO appraisalDTO;

    @BeforeEach
    void setUp() {
        appraisedUser = new UserEntity();
        appraisedUser.setId(1L);
        appraisingUser = new UserEntity();
        appraisingUser.setId(2L);
        cycle = new CycleEntity();
        cycle.setId(10L);
        cycle.setState(CycleState.OPEN);
        appraisalEntity = new AppraisalEntity();
        appraisalEntity.setId(100L);
        appraisalEntity.setAppraisedUser(appraisedUser);
        appraisalEntity.setAppraisingUser(appraisingUser);
        appraisalEntity.setCycle(cycle);
        appraisalEntity.setState(AppraisalState.IN_PROGRESS);
        appraisalEntity.setFeedback("Good job");
        appraisalEntity.setScore(3);
        appraisalEntity.setCreationDate(LocalDate.now());
        appraisalEntity.setEditedDate(LocalDate.now());

        createAppraisalDTO = new CreateAppraisalDTO();
        createAppraisalDTO.setAppraisedUserId(1L);
        createAppraisalDTO.setAppraisingUserId(2L);
        createAppraisalDTO.setCycleId(10L);
        createAppraisalDTO.setFeedback("Good job");
        createAppraisalDTO.setScore(3);

        updateAppraisalDTO = new UpdateAppraisalDTO();
        updateAppraisalDTO.setId(100L);
        updateAppraisalDTO.setFeedback("Updated feedback");
        updateAppraisalDTO.setScore(4);
        updateAppraisalDTO.setState(AppraisalState.COMPLETED);

        appraisalDTO = new AppraisalDTO();
        appraisalDTO.setId(100L);
        appraisalDTO.setAppraisedUserId(1L);
        appraisalDTO.setAppraisingUserId(2L);
        appraisalDTO.setCycleId(10L);
        appraisalDTO.setFeedback("Good job");
        appraisalDTO.setScore(3);
        appraisalDTO.setState(AppraisalState.IN_PROGRESS);
    }

    @Nested
    @DisplayName("createAppraisal")
    class CreateAppraisal {
        @Test
        void createsAppraisalSuccessfully() {
            when(userRepository.find(1L)).thenReturn(appraisedUser);
            when(userRepository.find(2L)).thenReturn(appraisingUser);
            when(cycleRepository.find(10L)).thenReturn(cycle);
            when(appraisalRepository.findAppraisalByUsersAndCycle(1L, 2L, 10L)).thenReturn(null);
            when(appraisalMapper.toDto(any(AppraisalEntity.class))).thenReturn(appraisalDTO);

            AppraisalDTO result = appraisalService.createAppraisal(createAppraisalDTO);
            assertNotNull(result);
            verify(appraisalRepository).persist(any(AppraisalEntity.class));
            assertEquals(1L, result.getAppraisedUserId());
            assertEquals(2L, result.getAppraisingUserId());
            assertEquals(10L, result.getCycleId());
        }

        @Test
        void throwsIfAppraisedUserNotFound() {
            when(userRepository.find(1L)).thenReturn(null);
            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                appraisalService.createAppraisal(createAppraisalDTO));
            assertTrue(ex.getMessage().contains("Appraised user not found"));
        }

        @Test
        void throwsIfAppraisingUserNotFound() {
            when(userRepository.find(1L)).thenReturn(appraisedUser);
            when(userRepository.find(2L)).thenReturn(null);
            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                appraisalService.createAppraisal(createAppraisalDTO));
            assertTrue(ex.getMessage().contains("Appraising user not found"));
        }

        @Test
        void throwsIfCycleNotFound() {
            when(userRepository.find(1L)).thenReturn(appraisedUser);
            when(userRepository.find(2L)).thenReturn(appraisingUser);
            when(cycleRepository.find(10L)).thenReturn(null);
            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                appraisalService.createAppraisal(createAppraisalDTO));
            assertTrue(ex.getMessage().contains("Cycle not found"));
        }

        @Test
        void throwsIfCycleClosed() {
            cycle.setState(CycleState.CLOSED);
            when(userRepository.find(1L)).thenReturn(appraisedUser);
            when(userRepository.find(2L)).thenReturn(appraisingUser);
            when(cycleRepository.find(10L)).thenReturn(cycle);
            Exception ex = assertThrows(IllegalStateException.class, () ->
                appraisalService.createAppraisal(createAppraisalDTO));
            assertTrue(ex.getMessage().contains("Cannot create appraisal in a closed cycle"));
        }

        @Test
        void throwsIfAppraisalAlreadyExists() {
            when(userRepository.find(1L)).thenReturn(appraisedUser);
            when(userRepository.find(2L)).thenReturn(appraisingUser);
            when(cycleRepository.find(10L)).thenReturn(cycle);
            when(appraisalRepository.findAppraisalByUsersAndCycle(1L, 2L, 10L)).thenReturn(appraisalEntity);
            Exception ex = assertThrows(IllegalStateException.class, () ->
                appraisalService.createAppraisal(createAppraisalDTO));
            assertTrue(ex.getMessage().contains("Appraisal already exists for this user in this cycle"));
        }

        @Test
        void throwsIfNotManagerOfUser() {
            // Simulate isManagerOfUser returns false
            when(userRepository.find(1L)).thenReturn(appraisedUser);
            when(userRepository.find(2L)).thenReturn(appraisingUser);
            when(cycleRepository.find(10L)).thenReturn(cycle);
            when(appraisalRepository.findAppraisalByUsersAndCycle(1L, 2L, 10L)).thenReturn(null);
            AppraisalService spyService = Mockito.spy(appraisalService);
            doReturn(false).when(spyService).isManagerOfUser(any(), any());
            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                spyService.createAppraisal(createAppraisalDTO));
            assertTrue(ex.getMessage().contains("User is not authorized to appraise this user"));
        }
    }

    @Nested
    @DisplayName("getAppraisalsByAppraisedUser")
    class GetAppraisalsByAppraisedUser {
        @Test
        void returnsListOfAppraisals() {
            List<AppraisalEntity> entities = Arrays.asList(appraisalEntity);
            when(appraisalRepository.findAppraisalsByAppraisedUser(1L)).thenReturn(entities);
            when(appraisalMapper.toDtoList(entities)).thenReturn(Arrays.asList(appraisalDTO));
            List<AppraisalDTO> result = appraisalService.getAppraisalsByAppraisedUser(1L);
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("getAppraisalsByManager")
    class GetAppraisalsByManager {
        @Test
        void returnsListOfAppraisals() {
            List<AppraisalEntity> entities = Arrays.asList(appraisalEntity);
            when(appraisalRepository.findAppraisalsByAppraisingUser(2L)).thenReturn(entities);
            when(appraisalMapper.toDtoList(entities)).thenReturn(Arrays.asList(appraisalDTO));
            List<AppraisalDTO> result = appraisalService.getAppraisalsByManager(2L);
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("getAppraisalsByCycle")
    class GetAppraisalsByCycle {
        @Test
        void returnsListOfAppraisals() {
            List<AppraisalEntity> entities = Arrays.asList(appraisalEntity);
            when(appraisalRepository.findAppraisalsByCycle(10L)).thenReturn(entities);
            when(appraisalMapper.toDtoList(entities)).thenReturn(Arrays.asList(appraisalDTO));
            List<AppraisalDTO> result = appraisalService.getAppraisalsByCycle(10L);
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("getAppraisalStats")
    class GetAppraisalStats {
        @Test
        void returnsStats() {
            when(appraisalRepository.countAppraisalsByUser(1L, true)).thenReturn(5L);
            when(appraisalRepository.countAppraisalsByUser(1L, false)).thenReturn(3L);
            AppraisalStatsDTO stats = appraisalService.getAppraisalStats(1L);
            assertNotNull(stats);
            assertEquals(1L, stats.getUserId());
            assertEquals(5L, stats.getReceivedAppraisalsCount());
            assertEquals(3L, stats.getGivenAppraisalsCount());
        }
    }

    @Nested
    @DisplayName("checkIfManagerOfUser")
    class CheckIfManagerOfUser {
        @Test
        void returnsTrueIfManagerMatches() {
            when(appraisalRepository.find(100L)).thenReturn(appraisalEntity);
            appraisalEntity.setAppraisingUser(appraisingUser);
            boolean result = appraisalService.checkIfManagerOfUser(100L, 2L);
            assertTrue(result);
        }

        @Test
        void returnsFalseIfManagerDoesNotMatch() {
            when(appraisalRepository.find(100L)).thenReturn(appraisalEntity);
            appraisalEntity.setAppraisingUser(appraisedUser); // id=1L
            boolean result = appraisalService.checkIfManagerOfUser(100L, 2L);
            assertFalse(result);
        }

        @Test
        void returnsFalseIfAppraisalNotFound() {
            when(appraisalRepository.find(100L)).thenReturn(null);
            boolean result = appraisalService.checkIfManagerOfUser(100L, 2L);
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("countAllAppraisals and countAppraisalsByState")
    class CountAppraisals {
        @Test
        void countAllAppraisalsReturnsValue() {
            when(appraisalRepository.getTotalAppraisalsWithFilters(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(42L);
            assertEquals(42L, appraisalService.countAllAppraisals());
        }

        @Test
        void countAppraisalsByStateReturnsValue() {
            when(appraisalRepository.getTotalAppraisalsWithFilters(any(), any(), any(), any(), any(), any(), any(), eq(AppraisalState.COMPLETED))).thenReturn(7L);
            assertEquals(7L, appraisalService.countAppraisalsByState(AppraisalState.COMPLETED));
        }
    }
}
