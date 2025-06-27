package pt.uc.dei.services;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.CycleDTO;
import pt.uc.dei.entities.AppraisalEntity;
import pt.uc.dei.dtos.CycleUpdateDTO;
import pt.uc.dei.entities.CycleEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.AppraisalState;
import pt.uc.dei.enums.CycleState;
import pt.uc.dei.mapper.CycleMapper;
import pt.uc.dei.repositories.AppraisalRepository;
import pt.uc.dei.repositories.CycleRepository;
import pt.uc.dei.repositories.UserRepository;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing cycle-related operations.
 * <p>
 * Provides functionality for cycle creation, retrieval, updating,
 * and filtering. Handles business logic and validation rules.
 *
 * @Stateless Marks this class as a stateless EJB, making it eligible for dependency injection
 * and transaction management by the EJB container.
 */
@Stateless
public class CycleService implements Serializable {

    /**
     * Logger instance for logging operations within this class.
     */
    private static final Logger LOGGER = LogManager.getLogger(CycleService.class);

    /**
     * Serial version UID for serialization support.
     */
    private static final long serialVersionUID = 1L;

    @EJB
    private CycleRepository cycleRepository;

    @EJB
    private UserRepository userRepository;

    @EJB
    private AppraisalRepository appraisalRepository;

    @Inject
    private CycleMapper cycleMapper;

    /**
     * Creates a new cycle and initializes appraisals for all active users.
     * Validates that all users have managers before creation.
     *
     * @param cycleDTO The DTO containing cycle creation data
     * @return The created cycle DTO
     * @throws IllegalArgumentException If validation fails
     * @throws IllegalStateException If business rules are violated
     */
    @Transactional
    public CycleDTO createCycle(CycleDTO cycleDTO) {
        LOGGER.info("Creating new cycle from {} to {} with admin {}", 
                   cycleDTO.getStartDate(), 
                   cycleDTO.getEndDate(),
                   cycleDTO.getAdminId());

        // VALIDATION 1: Check for users without managers BEFORE creating cycle
        List<UserEntity> usersWithoutManager = userRepository.findActiveUsersWithoutManager();
        if (!usersWithoutManager.isEmpty()) {
            List<String> userEmails = usersWithoutManager.stream()
                    .map(UserEntity::getEmail)
                    .collect(Collectors.toList());
            
            LOGGER.error("Cannot create cycle: {} active users without manager assigned: {}", 
                        usersWithoutManager.size(), userEmails);
            
            throw new IllegalStateException(
                String.format("Cannot create cycle. %d active user(s) without manager: %s", 
                            usersWithoutManager.size(), String.join(", ", userEmails))
            );
        }

        // VALIDATION 2: Validate admin user exists
        UserEntity admin = userRepository.find(cycleDTO.getAdminId());
        if (admin == null) {
            throw new IllegalArgumentException("Admin user not found");
        }

        // VALIDATION 3: Validate dates
        if (cycleDTO.getStartDate().isAfter(cycleDTO.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        if (cycleDTO.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }

        // VALIDATION 4: Check for overlapping cycles
        if (cycleRepository.hasOverlappingCycles(cycleDTO.getStartDate(), cycleDTO.getEndDate(), null)) {
            throw new IllegalStateException("A cycle already exists for the specified date range");
        }

        try {
            // Create new cycle
            CycleEntity cycleEntity = cycleMapper.toEntity(cycleDTO);
            cycleEntity.setAdmin(admin);
            cycleEntity.setState(CycleState.OPEN);

            // PERSIST CYCLE
            cycleRepository.persist(cycleEntity);
            LOGGER.info("Created cycle with ID: {}", cycleEntity.getId());

            // CREATE APPRAISALS FOR ALL ACTIVE USERS
            createAppraisalsForCycle(cycleEntity);

            return cycleMapper.toDto(cycleEntity);

        } catch (Exception e) {
            LOGGER.error("Error creating cycle: {}", cycleDTO.getId(), e);
            throw new RuntimeException("Failed to create cycle: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing cycle with the provided data.
     *
     * @param cycleUpdateDTO The DTO containing updated cycle data
     * @return The updated CycleDTO
     * @throws IllegalArgumentException If cycle not found or validation fails
     * @throws IllegalStateException If cycle cannot be modified
     */
    @Transactional
    public CycleDTO updateCycle(CycleUpdateDTO cycleUpdateDTO) {
        LOGGER.info("Updating cycle with ID: {}", cycleUpdateDTO.getId());

        CycleEntity cycle = cycleRepository.find(cycleUpdateDTO.getId());
        if (cycle == null) {
            throw new IllegalArgumentException("Cycle not found");
        }

        if (cycle.getState() == CycleState.CLOSED) {
            throw new IllegalStateException("Cannot modify a closed cycle");
        }

        // Validate dates
        if (cycleUpdateDTO.getStartDate().isAfter(cycleUpdateDTO.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        // Check for overlapping cycles (excluding current cycle)
        if (cycleRepository.hasOverlappingCycles(cycleUpdateDTO.getStartDate(), cycleUpdateDTO.getEndDate(), cycle.getId())) {
            throw new IllegalStateException("Another cycle already exists for the specified date range");
        }

        // Update admin if provided
        if (cycleUpdateDTO.getAdminId() != null && !cycleUpdateDTO.getAdminId().equals(cycle.getAdmin().getId())) {
            UserEntity newAdmin = userRepository.find(cycleUpdateDTO.getAdminId());
            if (newAdmin == null) {
                throw new IllegalArgumentException("New admin user not found");
            }
            cycle.setAdmin(newAdmin);
        }

        // Update other fields
        cycleMapper.updateEntityFromDto(cycleUpdateDTO, cycle);

        cycleRepository.merge(cycle);
        LOGGER.info("Updated cycle with ID: {}", cycle.getId());

        return cycleMapper.toDto(cycle);
    }

    /**
     * Retrieves a cycle by its ID.
     *
     * @param id The cycle ID
     * @return The cycle DTO
     * @throws IllegalArgumentException If cycle not found
     */
    public CycleDTO getCycleById(Long id) {
        LOGGER.debug("Retrieving cycle with ID: {}", id);

        CycleEntity cycle = cycleRepository.find(id);
        if (cycle == null) {
            throw new IllegalArgumentException("Cycle not found");
        }

        return cycleMapper.toDto(cycle);
    }

    /**
     * Retrieves all cycles.
     *
     * @return List of all cycle DTOs
     */
    public List<CycleDTO> getAllCycles() {
        LOGGER.debug("Retrieving all cycles");
        List<CycleEntity> cycles = cycleRepository.getAllCycles();
        return cycleMapper.toDtoList(cycles);
    }

    /**
     * Retrieves cycles with a specific state.
     *
     * @param state The cycle state to filter by
     * @return List of cycle DTOs
     */
    public List<CycleDTO> getCyclesByState(CycleState state) {
        LOGGER.debug("Retrieving cycles with state: {}", state);

        List<CycleEntity> cycles = cycleRepository.findCyclesByState(state);
        return cycleMapper.toDtoList(cycles);
    }

    /**
     * Retrieves the current active cycle.
     *
     * @return The current active cycle DTO, or null if none exists
     */
    public CycleDTO getCurrentActiveCycle() {
        LOGGER.debug("Retrieving current active cycle");

        CycleEntity cycle = cycleRepository.findCurrentActiveCycle();
        return cycle != null ? cycleMapper.toDto(cycle) : null;
    }

    /**
     * Retrieves cycles managed by a specific administrator.
     *
     * @param adminId The administrator ID
     * @return List of cycle DTOs
     */
    public List<CycleDTO> getCyclesByAdmin(Long adminId) {
        LOGGER.debug("Retrieving cycles for admin ID: {}", adminId);

        List<CycleEntity> cycles = cycleRepository.findCyclesByAdmin(adminId);
        return cycleMapper.toDtoList(cycles);
    }

    /**
     * Retrieves upcoming cycles.
     *
     * @return List of upcoming cycle DTOs
     */
    public List<CycleDTO> getUpcomingCycles() {
        LOGGER.debug("Retrieving upcoming cycles");

        List<CycleEntity> cycles = cycleRepository.findUpcomingCycles();
        return cycleMapper.toDtoList(cycles);
    }

    /**
     * Retrieves cycles with advanced filtering options.
     *
     * @param state Optional filter by cycle state
     * @param adminId Optional filter by administrator ID
     * @param startDateFrom Optional filter for cycles starting after this date
     * @param startDateTo Optional filter for cycles starting before this date
     * @param limit Maximum number of results
     * @param offset Starting position for pagination
     * @return List of filtered cycle DTOs
     */
    public List<CycleDTO> getCyclesWithFilters(CycleState state, Long adminId,
                                              LocalDate startDateFrom, LocalDate startDateTo,
                                              Integer limit, Integer offset) {
        LOGGER.debug("Retrieving cycles with filters");

        List<CycleEntity> cycles = cycleRepository.findCyclesWithFilters(
            state, adminId, startDateFrom, startDateTo, limit, offset
        );
        return cycleMapper.toDtoList(cycles);
    }

    /**
     * Closes a cycle, changing its state to CLOSED.
     *
     * @param cycleId The cycle ID
     * @return The updated cycle DTO
     * @throws IllegalArgumentException If cycle not found
     * @throws IllegalStateException If cycle is already closed
     */
    @Transactional
    public CycleDTO closeCycle(Long cycleId) {
        LOGGER.info("Closing cycle with ID: {}", cycleId);

        CycleEntity cycle = cycleRepository.find(cycleId);
        if (cycle == null) {
            throw new IllegalArgumentException("Cycle not found");
        }

        if (cycle.getState() == CycleState.CLOSED) {
            throw new IllegalStateException("Cycle is already closed");
        }

        cycle.setState(CycleState.CLOSED);
        cycleRepository.merge(cycle);
        LOGGER.info("Closed cycle with ID: {}", cycleId);

        return cycleMapper.toDto(cycle);
    }

    /**
     * Reopens a cycle, changing its state to OPEN.
     *
     * @param cycleId The cycle ID
     * @return The updated cycle DTO
     * @throws IllegalArgumentException If cycle not found
     * @throws IllegalStateException If cycle cannot be reopened
     */
    @Transactional
    public CycleDTO reopenCycle(Long cycleId) {
        LOGGER.info("Reopening cycle with ID: {}", cycleId);

        CycleEntity cycle = cycleRepository.find(cycleId);
        if (cycle == null) {
            throw new IllegalArgumentException("Cycle not found");
        }

        if (cycle.getState() == CycleState.OPEN) {
            throw new IllegalStateException("Cycle is already open");
        }

        // Check if reopening would create overlapping cycles
        if (cycleRepository.hasOverlappingCycles(cycle.getStartDate(), cycle.getEndDate(), cycle.getId())) {
            throw new IllegalStateException("Cannot reopen cycle: would overlap with existing open cycles");
        }

        cycle.setState(CycleState.OPEN);
        cycleRepository.merge(cycle);
        LOGGER.info("Reopened cycle with ID: {}", cycleId);

        return cycleMapper.toDto(cycle);
    }

    /**
     * Deletes a cycle by its ID if it has not started yet.
     *
     * @param cycleId The cycle ID
     * @throws IllegalArgumentException If cycle not found
     * @throws IllegalStateException If cycle cannot be deleted
     */
    @Transactional
    public void deleteCycle(Long cycleId) {
        LOGGER.info("Deleting cycle with ID: {}", cycleId);

        CycleEntity cycle = cycleRepository.find(cycleId);
        if (cycle == null) {
            throw new IllegalArgumentException("Cycle not found");
        }

        // Business rule: Can only delete cycles that haven't started yet
        if (cycle.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot delete a cycle that has already started");
        }

        // Additional check: Could also verify no appraisals exist for this cycle
        // This would require injecting AppraisalRepository and checking count

        cycleRepository.remove(cycle);
        LOGGER.info("Deleted cycle with ID: {}", cycleId);
    }

    /**
     * Automatically closes all expired cycles (end date in the past).
     *
     * @return The number of cycles that were closed
     */
    @Transactional
    public int closeExpiredCycles() {
        LOGGER.info("Closing expired cycles");

        List<CycleEntity> expiredCycles = cycleRepository.findExpiredOpenCycles();
        
        for (CycleEntity cycle : expiredCycles) {
            cycle.setState(CycleState.CLOSED);
            cycleRepository.merge(cycle);
            LOGGER.info("Auto-closed expired cycle with ID: {}", cycle.getId());
        }

        int closedCount = expiredCycles.size();
        LOGGER.info("Closed {} expired cycles", closedCount);
        
        return closedCount;
    }

    /**
     * Creates a single empty appraisal entity.
     *
     * @param appraisedUser The user being appraised
     * @param appraisingUser The manager doing the appraisal
     * @param cycle The cycle this appraisal belongs to
     * @return The created appraisal entity
     */
    private AppraisalEntity createEmptyAppraisal(UserEntity appraisedUser, UserEntity appraisingUser, CycleEntity cycle) {
        AppraisalEntity appraisal = new AppraisalEntity();
        
        // Set relationships
        appraisal.setAppraisedUser(appraisedUser);
        appraisal.setAppraisingUser(appraisingUser);
        appraisal.setCycle(cycle);
        
        // Set initial state and data
        appraisal.setState(AppraisalState.IN_PROGRESS);
        appraisal.setFeedback(null);  // Empty initially
        appraisal.setScore(null);     // Empty initially
        appraisal.setCreationDate(LocalDate.now());
        appraisal.setEditedDate(LocalDate.now());
        
        LOGGER.trace("Created empty appraisal entity for user {} by manager {} in cycle {}", 
                    appraisedUser.getEmail(), appraisingUser.getEmail(), cycle.getId());
        
        return appraisal;
    }

    /**
     * Creates empty appraisals for all active users in the given cycle.
     * Each user receives an appraisal from their assigned manager.
     *
     * @param cycle The cycle for which to create appraisals
     * @throws IllegalStateException If any user has no manager
     */
    @Transactional
    private void createAppraisalsForCycle(CycleEntity cycle) {
        LOGGER.info("Creating appraisals for cycle ID: {}", cycle.getId());

        // Get all active users
        List<UserEntity> activeUsers = userRepository.findActiveUsersForCycle();
        LOGGER.debug("Found {} active users for appraisal creation", activeUsers.size());
        
        if (activeUsers.isEmpty()) {
            LOGGER.warn("No active users found for appraisal creation in cycle ID: {}", cycle.getId());
            return;
        }

        int createdCount = 0;
        int skippedCount = 0;

        for (UserEntity user : activeUsers) {
            try {
                // Double-check manager exists (redundant safety check)
                if (user.getManagerUser() == null) {
                    LOGGER.error("CRITICAL: User {} (ID: {}) has no manager during appraisal creation", 
                                user.getEmail(), user.getId());
                    throw new IllegalStateException("User " + user.getEmail() + " has no manager assigned");
                }

                // Check if appraisal already exists (safety check)
                AppraisalEntity existingAppraisal = appraisalRepository.findAppraisalByUsersAndCycle(
                    user.getId(), user.getManagerUser().getId(), cycle.getId()
                );

                if (existingAppraisal != null) {
                    LOGGER.warn("Appraisal already exists for user {} in cycle {} - skipping", 
                               user.getEmail(), cycle.getId());
                    skippedCount++;
                    continue;
                }

                // Create empty appraisal
                AppraisalEntity appraisal = createEmptyAppraisal(user, user.getManagerUser(), cycle);
                appraisalRepository.persist(appraisal);
                
                createdCount++;
                LOGGER.debug("Created appraisal ID: {} for user {} (ID: {}) by manager {} (ID: {})", 
                            appraisal.getId(),
                            user.getEmail(), user.getId(), 
                            user.getManagerUser().getEmail(), user.getManagerUser().getId());

            } catch (Exception e) {
                LOGGER.error("Error creating appraisal for user {} (ID: {}) in cycle {}", 
                            user.getEmail(), user.getId(), cycle.getId(), e);
                throw new RuntimeException("Failed to create appraisal for user: " + user.getEmail(), e);
            }
        }

        LOGGER.info("Appraisal creation summary for cycle ID: {} - Created: {}, Skipped: {}, Total Users: {}", 
                   cycle.getId(), createdCount, skippedCount, activeUsers.size());

        if (createdCount == 0 && skippedCount == 0) {
            LOGGER.warn("No appraisals were created for cycle ID: {}", cycle.getId());
        }
    }
}
