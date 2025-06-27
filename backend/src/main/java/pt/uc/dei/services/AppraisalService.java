package pt.uc.dei.services;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.AppraisalDTO;
import pt.uc.dei.dtos.CreateAppraisalDTO;
import pt.uc.dei.dtos.UpdateAppraisalDTO;
import pt.uc.dei.entities.AppraisalEntity;
import pt.uc.dei.enums.AppraisalState;
import pt.uc.dei.enums.CycleState;
import pt.uc.dei.entities.CycleEntity;
import pt.uc.dei.repositories.CycleRepository;
import pt.uc.dei.repositories.AppraisalRepository;
import pt.uc.dei.repositories.UserRepository;
import pt.uc.dei.mapper.AppraisalMapper;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing appraisal-related operations.
 * <p>
 * Provides functionality for appraisal creation, retrieval, updating,
 * and filtering. Handles business logic and validation rules.
 *
 * @Stateless Marks this class as a stateless EJB, making it eligible for dependency injection
 * and transaction management by the EJB container.
 */
@Stateless
public class AppraisalService implements Serializable {

    /**
     * Logger instance for logging operations within this class.
     */
    private static final Logger LOGGER = LogManager.getLogger(AppraisalService.class);

    /**
     * Serial version UID for serialization support.
     */
    private static final long serialVersionUID = 1L;

    @EJB
    private AppraisalRepository appraisalRepository;

    @EJB
    private UserRepository userRepository;

    @EJB
    private CycleRepository cycleRepository;

    @Inject
    private AppraisalMapper appraisalMapper;

    /**
     * Creates a new appraisal.
     *
     * @param createAppraisalDTO The DTO containing appraisal creation data
     * @return The created appraisal DTO
     * @throws IllegalArgumentException If validation fails
     * @throws IllegalStateException If business rules are violated
     */
    @Transactional
    public AppraisalDTO createAppraisal(CreateAppraisalDTO createAppraisalDTO) {
        LOGGER.info("Creating new appraisal for user {} by user {} in cycle {}", 
                   createAppraisalDTO.getAppraisedUserId(), 
                   createAppraisalDTO.getAppraisingUserId(),
                   createAppraisalDTO.getCycleId());

        // Validate users exist
        UserEntity appraisedUser = userRepository.find(createAppraisalDTO.getAppraisedUserId());
        if (appraisedUser == null) {
            throw new IllegalArgumentException("Appraised user not found");
        }

        UserEntity appraisingUser = userRepository.find(createAppraisalDTO.getAppraisingUserId());
        if (appraisingUser == null) {
            throw new IllegalArgumentException("Appraising user not found");
        }

        // Validate cycle exists and is active
        CycleEntity cycle = cycleRepository.find(createAppraisalDTO.getCycleId());
        if (cycle == null) {
            throw new IllegalArgumentException("Cycle not found");
        }

        if (cycle.getState() != CycleState.OPEN) {
            throw new IllegalStateException("Cannot create appraisal in a closed cycle");
        }

        // Check if appraisal already exists for this combination
        AppraisalEntity existingAppraisal = appraisalRepository.findAppraisalByUsersAndCycle(
            createAppraisalDTO.getAppraisedUserId(),
            createAppraisalDTO.getAppraisingUserId(),
            createAppraisalDTO.getCycleId()
        );

        if (existingAppraisal != null) {
            throw new IllegalStateException("Appraisal already exists for this user in this cycle");
        }

        // Validate that appraising user is manager of appraised user
        if (!isManagerOfUser(appraisingUser, appraisedUser)) {
            throw new IllegalArgumentException("User is not authorized to appraise this user");
        }

        // Create new appraisal
        AppraisalEntity appraisalEntity = new AppraisalEntity();
        appraisalEntity.setAppraisedUser(appraisedUser);
        appraisalEntity.setAppraisingUser(appraisingUser);
        appraisalEntity.setCycle(cycle);
        appraisalEntity.setFeedback(createAppraisalDTO.getFeedback());
        appraisalEntity.setScore(createAppraisalDTO.getScore());
        appraisalEntity.setState(AppraisalState.IN_PROGRESS);
        appraisalEntity.setCreationDate(LocalDateTime.now());
        appraisalEntity.setEditedDate(LocalDateTime.now());        appraisalRepository.persist(appraisalEntity);
        LOGGER.info("Created appraisal with ID: {}", appraisalEntity.getId());

        return appraisalMapper.toDto(appraisalEntity);
    }

    /**
     * Updates an existing appraisal.
     *
     * @param updateAppraisalDTO The DTO containing updated appraisal data
     * @return The updated appraisal DTO
     * @throws IllegalArgumentException If appraisal not found
     * @throws IllegalStateException If appraisal cannot be modified
     */
    @Transactional
    public AppraisalDTO updateAppraisal(UpdateAppraisalDTO updateAppraisalDTO) {
        LOGGER.info("Updating appraisal with ID: {}", updateAppraisalDTO.getId());

        AppraisalEntity appraisal = appraisalRepository.find(updateAppraisalDTO.getId());
        if (appraisal == null) {
            throw new IllegalArgumentException("Appraisal not found");
        }

        if (appraisal.getState() == AppraisalState.CLOSED) {
            throw new IllegalStateException("Cannot modify a closed appraisal");
        }

        if (appraisal.getCycle().getState() != CycleState.OPEN) {
            throw new IllegalStateException("Cannot modify appraisal in a closed cycle");
        }

        // Update fields
        appraisal.setFeedback(updateAppraisalDTO.getFeedback());
        appraisal.setScore(updateAppraisalDTO.getScore());
        appraisal.setEditedDate(LocalDateTime.now());        appraisalRepository.merge(appraisal);
        LOGGER.info("Updated appraisal with ID: {}", appraisal.getId());

        return appraisalMapper.toDto(appraisal);
    }

    /**
     * Retrieves an appraisal by its ID.
     *
     * @param id The appraisal ID
     * @return The appraisal DTO
     * @throws IllegalArgumentException If appraisal not found
     */
    public AppraisalDTO getAppraisalById(Long id) {
        LOGGER.debug("Retrieving appraisal with ID: {}", id);

        AppraisalEntity appraisal = appraisalRepository.find(id);
        if (appraisal == null) {
            throw new IllegalArgumentException("Appraisal not found");
        }

        return appraisalMapper.toDto(appraisal);
    }

    /**
     * Retrieves all appraisals for a specific user (as appraised).
     *
     * @param userId The user ID
     * @return List of appraisal DTOs
     */
    public List<AppraisalDTO> getAppraisalsByAppraisedUser(Long userId) {
        LOGGER.debug("Retrieving appraisals for appraised user ID: {}", userId);

        List<AppraisalEntity> appraisals = appraisalRepository.findAppraisalsByAppraisedUser(userId);
        return appraisalMapper.toDtoList(appraisals);
    }

    /**
     * Retrieves all appraisals created by a specific manager.
     *
     * @param managerId The manager user ID
     * @return List of appraisal DTOs
     */
    public List<AppraisalDTO> getAppraisalsByManager(Long managerId) {
        LOGGER.debug("Retrieving appraisals created by manager ID: {}", managerId);

        List<AppraisalEntity> appraisals = appraisalRepository.findAppraisalsByAppraisingUser(managerId);
        return appraisalMapper.toDtoList(appraisals);
    }

    /**
     * Retrieves all appraisals within a specific cycle.
     *
     * @param cycleId The cycle ID
     * @return List of appraisal DTOs
     */
    public List<AppraisalDTO> getAppraisalsByCycle(Long cycleId) {
        LOGGER.debug("Retrieving appraisals for cycle ID: {}", cycleId);

        List<AppraisalEntity> appraisals = appraisalRepository.findAppraisalsByCycle(cycleId);
        return appraisalMapper.toDtoList(appraisals);
    }

    /**
     * Retrieves appraisals with advanced filtering options.
     *
     * @param appraisedUserId Optional filter by appraised user ID
     * @param appraisingUserId Optional filter by appraising user ID
     * @param cycleId Optional filter by cycle ID
     * @param state Optional filter by appraisal state
     * @param limit Maximum number of results
     * @param offset Starting position for pagination
     * @return List of filtered appraisal DTOs
     */
    public List<AppraisalDTO> getAppraisalsWithFilters(Long appraisedUserId, Long appraisingUserId,
                                                      Long cycleId, AppraisalState state,
                                                      Integer limit, Integer offset) {
        LOGGER.debug("Retrieving appraisals with filters");

        List<AppraisalEntity> appraisals = appraisalRepository.findAppraisalsWithFilters(
            appraisedUserId, appraisingUserId, cycleId, state, limit, offset
        );
        return appraisalMapper.toDtoList(appraisals);
    }

    /**
     * Completes an appraisal, changing its state to COMPLETED.
     *
     * @param appraisalId The appraisal ID
     * @return The updated appraisal DTO
     * @throws IllegalArgumentException If appraisal not found
     * @throws IllegalStateException If appraisal cannot be completed
     */
    @Transactional
    public AppraisalDTO completeAppraisal(Long appraisalId) {
        LOGGER.info("Completing appraisal with ID: {}", appraisalId);

        AppraisalEntity appraisal = appraisalRepository.find(appraisalId);
        if (appraisal == null) {
            throw new IllegalArgumentException("Appraisal not found");
        }

        if (appraisal.getState() == AppraisalState.CLOSED) {
            throw new IllegalStateException("Cannot complete a closed appraisal");
        }

        appraisal.setState(AppraisalState.COMPLETED);
        appraisal.setEditedDate(LocalDateTime.now());        appraisalRepository.merge(appraisal);
        LOGGER.info("Completed appraisal with ID: {}", appraisal.getId());

        return appraisalMapper.toDto(appraisal);
    }

    /**
     * Closes an appraisal, changing its state to CLOSED.
     *
     * @param appraisalId The appraisal ID
     * @return The updated appraisal DTO
     * @throws IllegalArgumentException If appraisal not found
     */
    @Transactional
    public AppraisalDTO closeAppraisal(Long appraisalId) {
        LOGGER.info("Closing appraisal with ID: {}", appraisalId);

        AppraisalEntity appraisal = appraisalRepository.find(appraisalId);
        if (appraisal == null) {
            throw new IllegalArgumentException("Appraisal not found");
        }

        appraisal.setState(AppraisalState.CLOSED);
        appraisal.setEditedDate(LocalDateTime.now());        appraisalRepository.merge(appraisal);
        LOGGER.info("Closed appraisal with ID: {}", appraisal.getId());

        return appraisalMapper.toDto(appraisal);
    }

    /**
     * Deletes an appraisal by its ID.
     *
     * @param appraisalId The appraisal ID
     * @throws IllegalArgumentException If appraisal not found
     * @throws IllegalStateException If appraisal cannot be deleted
     */
    @Transactional
    public void deleteAppraisal(Long appraisalId) {
        LOGGER.info("Deleting appraisal with ID: {}", appraisalId);

        AppraisalEntity appraisal = appraisalRepository.find(appraisalId);
        if (appraisal == null) {
            throw new IllegalArgumentException("Appraisal not found");
        }

        if (appraisal.getState() == AppraisalState.CLOSED) {
            throw new IllegalStateException("Cannot delete a closed appraisal");
        }        appraisalRepository.remove(appraisal);
        LOGGER.info("Deleted appraisal with ID: {}", appraisalId);
    }

    /**
     * Gets appraisal statistics for a user.
     *
     * @param userId The user ID
     * @return Map containing appraisal statistics
     */
    public AppraisalStatsDTO getAppraisalStats(Long userId) {
        LOGGER.debug("Getting appraisal statistics for user ID: {}", userId);

        Long receivedCount = appraisalRepository.countAppraisalsByUser(userId, true);
        Long givenCount = appraisalRepository.countAppraisalsByUser(userId, false);

        AppraisalStatsDTO stats = new AppraisalStatsDTO();
        stats.setUserId(userId);
        stats.setReceivedAppraisalsCount(receivedCount);
        stats.setGivenAppraisalsCount(givenCount);

        return stats;
    }

    /**
     * Checks if a user is a manager of another user.
     * This is a simplified implementation - adjust based on your business logic.
     *
     * @param manager The potential manager
     * @param user The user to check
     * @return True if manager is authorized to appraise the user
     */
    private boolean isManagerOfUser(UserEntity manager, UserEntity user) {
        // This is a simplified check. In a real implementation, you might:
        // 1. Check if manager.getId().equals(user.getManagerUser().getId())
        // 2. Check role hierarchy
        // 3. Check organizational structure
        
        // For now, we'll assume any user can appraise any other user
        // You should implement proper authorization logic here
        return !manager.getId().equals(user.getId()); // Can't appraise yourself
    }

    /**
     * Inner class for appraisal statistics.
     */
    public static class AppraisalStatsDTO {
        private Long userId;
        private Long receivedAppraisalsCount;
        private Long givenAppraisalsCount;

        // Getters and setters
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getReceivedAppraisalsCount() {
            return receivedAppraisalsCount;
        }

        public void setReceivedAppraisalsCount(Long receivedAppraisalsCount) {
            this.receivedAppraisalsCount = receivedAppraisalsCount;
        }

        public Long getGivenAppraisalsCount() {
            return givenAppraisalsCount;
        }

        public void setGivenAppraisalsCount(Long givenAppraisalsCount) {
            this.givenAppraisalsCount = givenAppraisalsCount;
        }
    }

    /**
     * Closes specific appraisals by their IDs.
     * Only COMPLETED appraisals in OPEN cycles can be closed.
     *
     * @param appraisalIds List of appraisal IDs to close
     * @return Number of successfully closed appraisals
     * @throws IllegalArgumentException If no valid appraisals found
     */
    @Transactional
    public int closeAppraisalsByIds(List<Long> appraisalIds) {
        LOGGER.info("Attempting to close appraisals by IDs: {}", appraisalIds);
        
        if (appraisalIds == null || appraisalIds.isEmpty()) {
            throw new IllegalArgumentException("No appraisal IDs provided");
        }

        List<AppraisalEntity> validAppraisals = appraisalRepository.findValidAppraisalsForClosing(appraisalIds);
        
        if (validAppraisals.isEmpty()) {
            LOGGER.warn("No valid COMPLETED appraisals found in OPEN cycles for IDs: {}", appraisalIds);
            throw new IllegalArgumentException("No valid appraisals found for closing. Only COMPLETED appraisals in OPEN cycles can be closed.");
        }

        int closedCount = 0;
        for (AppraisalEntity appraisal : validAppraisals) {
            appraisal.setState(AppraisalState.CLOSED);
            appraisalRepository.merge(appraisal);
            closedCount++;
            LOGGER.debug("Closed appraisal ID: {} for user: {}", appraisal.getId(), appraisal.getAppraisedUser().getId());
        }
        
        LOGGER.info("Successfully closed {} appraisals by IDs", closedCount);
        return closedCount;
    }

    /**
     * Closes all COMPLETED appraisals in a specific cycle.
     * Only works if the cycle is OPEN.
     *
     * @param cycleId The cycle ID
     * @return Number of successfully closed appraisals
     * @throws IllegalArgumentException If cycle not found or not OPEN
     */
    @Transactional
    public int closeCompletedAppraisalsByCycleId(Long cycleId) {
        LOGGER.info("Attempting to close all COMPLETED appraisals in cycle ID: {}", cycleId);

        // Verify cycle exists and is OPEN
        CycleEntity cycle = cycleRepository.find(cycleId);
        if (cycle == null) {
            throw new IllegalArgumentException("Cycle not found with ID: " + cycleId);
        }
        if (cycle.getState() != CycleState.OPEN) {
            throw new IllegalStateException("Cannot close appraisals in a CLOSED cycle. Cycle ID: " + cycleId);
        }

        List<AppraisalEntity> completedAppraisals = appraisalRepository.findCompletedAppraisalsByCycleId(cycleId);
        
        if (completedAppraisals.isEmpty()) {
            LOGGER.info("No COMPLETED appraisals found in cycle ID: {}", cycleId);
            return 0;
        }

        int closedCount = 0;
        for (AppraisalEntity appraisal : completedAppraisals) {
            appraisal.setState(AppraisalState.CLOSED);
            appraisalRepository.merge(appraisal);
            closedCount++;
            LOGGER.debug("Closed appraisal ID: {} in cycle: {}", appraisal.getId(), cycleId);
        }
        
        LOGGER.info("Successfully closed {} COMPLETED appraisals in cycle ID: {}", closedCount, cycleId);
        return closedCount;
    }

    /**
     * Closes all COMPLETED appraisals for a specific user.
     * Only works for appraisals in OPEN cycles.
     *
     * @param userId The user ID (appraised user)
     * @return Number of successfully closed appraisals
     * @throws IllegalArgumentException If user not found
     */
    @Transactional
    public int closeCompletedAppraisalsByUserId(Long userId) {
        LOGGER.info("Attempting to close all COMPLETED appraisals for user ID: {}", userId);

        // Verify user exists
        UserEntity user = userRepository.find(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        List<AppraisalEntity> completedAppraisals = appraisalRepository.findCompletedAppraisalsByUserId(userId);
        
        if (completedAppraisals.isEmpty()) {
            LOGGER.info("No COMPLETED appraisals found for user ID: {}", userId);
            return 0;
        }

        int closedCount = 0;
        for (AppraisalEntity appraisal : completedAppraisals) {
            appraisal.setState(AppraisalState.CLOSED);
            appraisalRepository.merge(appraisal);
            closedCount++;
            LOGGER.debug("Closed appraisal ID: {} for user: {}", appraisal.getId(), userId);
        }
        
        LOGGER.info("Successfully closed {} COMPLETED appraisals for user ID: {}", closedCount, userId);
        return closedCount;
    }

    /**
     * Closes all COMPLETED appraisals in all OPEN cycles.
     * Administrative operation.
     *
     * @return Number of successfully closed appraisals
     */
    @Transactional
    public int closeAllCompletedAppraisals() {
        LOGGER.info("Attempting to close ALL COMPLETED appraisals in OPEN cycles");

        List<AppraisalEntity> completedAppraisals = appraisalRepository.findAllCompletedAppraisalsInOpenCycles();
        
        if (completedAppraisals.isEmpty()) {
            LOGGER.info("No COMPLETED appraisals found in OPEN cycles");
            return 0;
        }

        int closedCount = 0;
        for (AppraisalEntity appraisal : completedAppraisals) {
            appraisal.setState(AppraisalState.CLOSED);
            appraisalRepository.merge(appraisal);
            closedCount++;
            LOGGER.debug("Closed appraisal ID: {} in cycle: {}", appraisal.getId(), appraisal.getCycle().getId());
        }
        
        LOGGER.info("Successfully closed {} COMPLETED appraisals across all OPEN cycles", closedCount);
        return closedCount;
    }
}
