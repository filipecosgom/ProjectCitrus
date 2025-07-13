package pt.uc.dei.services;

import pt.uc.dei.dtos.FinishedCourseDTO;
import pt.uc.dei.mapper.FinishedCourseMapper;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.*;
import pt.uc.dei.repositories.AppraisalRepository;
import pt.uc.dei.repositories.CourseRepository;
import pt.uc.dei.repositories.FinishedCourseRepository;
import pt.uc.dei.utils.CSVGenerator;
import pt.uc.dei.utils.JWTUtil;
import pt.uc.dei.entities.ActivationTokenEntity;
import pt.uc.dei.entities.CourseEntity;
import pt.uc.dei.entities.FinishedCourseEntity;
import pt.uc.dei.entities.TemporaryUserEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.*;
import pt.uc.dei.mapper.UserMapper;
import pt.uc.dei.repositories.ActivationTokenRepository;
import pt.uc.dei.repositories.TemporaryUserRepository;
import pt.uc.dei.repositories.UserRepository;
import pt.uc.dei.utils.PasswordUtils;
import pt.uc.dei.utils.TwoFactorUtil;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for managing user-related operations.
 * <p>
 * Provides functionality for user verification, registration, activation,
 * and temporary user management. Utilizes {@link UserRepository},
 * {@link TemporaryUserRepository}, and {@link ActivationTokenRepository} for
 * persistence operations.
 *
 * @Stateless Marks this class as a stateless EJB, making it eligible for
 *            dependency injection
 *            and transaction management by the EJB container.
 */
@Stateless
public class UserService implements Serializable {
    /**
     * Logger instance for logging user service operations.
     */
    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    /**
     * Serial version UID for ensuring class consistency during serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Injected repository for managing permanent users.
     */
    @EJB
    UserRepository userRepository;

    @EJB
    AppraisalRepository appraisalRepository;

    /**
     * Injected repository for managing temporary users.
     */
    @EJB
    TemporaryUserRepository temporaryUserRepository;

    /**
     * Injected service for token-related operations.
     */
    @EJB
    TokenService tokenService;

    @EJB
    CourseRepository courseRepository;

    @EJB
    FinishedCourseRepository finishedCourseRepository;

    @Inject
    JWTUtil jwtUtil;

    @Inject
    UserMapper userMapper;

    @Inject
    FinishedCourseMapper finishedCourseMapper;

    @Inject
    TwoFactorUtil twoFactorUtil;

    /**
     * Injected repository for activation token persistence.
     */
    @EJB
    ActivationTokenRepository activationTokenRepository;

    /**
     * Checks if a user with the given email exists in the system.
     * <p>
     * Searches both permanent and temporary repositories to verify whether the
     * email is registered.
     *
     * @param email The email address to check for existence.
     * @return {@code true} if a user (permanent or temporary) exists with the
     *         email, {@code false} otherwise.
     * @throws jakarta.persistence.PersistenceException If an error occurs during
     *                                                  database operations.
     */
    public boolean findIfUserExists(String email) {
        UserEntity user = userRepository.findUserByEmail(email);
        TemporaryUserEntity temporaryUser = temporaryUserRepository.findTemporaryUserByEmail(email);
        return (temporaryUser != null || user != null);
    }

    /**
     * Registers a new temporary user in the system.
     * <p>
     * Generates an activation token and persists the temporary user in the
     * repository.
     *
     * @param newUser The temporary user data transfer object.
     * @return The generated activation token for the new user.
     */
    @Transactional
    public Map<String, String> registerUser(TemporaryUserDTO newUser) {
        TemporaryUserEntity user = new TemporaryUserEntity();
        user.setEmail(newUser.getEmail());
        user.setPassword(PasswordUtils.encrypt(newUser.getPassword()));
        ActivationTokenEntity token = new ActivationTokenEntity();
        token.setTokenValue(tokenService.generateNewToken());
        token.setCreationDate(LocalDateTime.now());
        token.setTemporaryUser(user);
        user.setActivationToken(token);
        GoogleAuthenticatorKey googleAuthenticatorKey = TwoFactorUtil.generateSecretKey();
        String secretKey = TwoFactorUtil.getSecretKeyString(googleAuthenticatorKey);
        user.setSecretKey(secretKey);
        temporaryUserRepository.persist(user);
        Map<String, String> codes = new HashMap<>();
        codes.put("token", token.getTokenValue());
        codes.put("secretKey", secretKey);

        LOGGER.info("New user created with email {} and activation token {}", newUser.getEmail(),
                token.getTokenValue());
        return codes;
    }

    /**
     * Updates an existing user with the provided data.
     * Only non-null fields in the DTO are updated.
     *
     * @param id            The ID of the user to update
     * @param updateUserDTO The DTO containing updated user data
     * @return true if the update was successful, false otherwise
     */
    @Transactional
    public Boolean updateUser(Long id, UpdateUserDTO updateUserDTO) {
        // Fetch the user from the repository
        UserEntity user = userRepository.findUserById(id);
        if (user == null) {
            LOGGER.error("Update user - user not found");
            return false;
        }
        // Update only those fields that are non-null in the DTO.
        if (updateUserDTO.getManagerId() != null) {
            UserEntity previousManager = user.getManagerUser();
            UserEntity newManager = userRepository.findUserById(updateUserDTO.getManagerId());
            if (newManager != null) {
                // 1. Move all appraisals to new manager
                appraisalRepository.setAppraisalsToNewManager(user.getId(), newManager.getId());
                // 2. Set new manager as user's manager
                user.setManagerUser(newManager);
                newManager.setUserIsManager(true);
                userRepository.merge(user);
                userRepository.merge(newManager);
            }
            // 3. If there was a previous manager, check if they still manage anyone
            if (previousManager != null && !checkIfUserStillIsManager(previousManager.getId())) {
                updateManagerStatus(previousManager);
            }
        }

        if (updateUserDTO.getUserIsManager() != null) {
            user.setUserIsManager(updateUserDTO.getUserIsManager());
        }
        if (updateUserDTO.getHasAvatar() != null) {
            user.setHasAvatar(updateUserDTO.getHasAvatar());
        }
        if (updateUserDTO.getName() != null) {
            user.setName(updateUserDTO.getName());
        }
        if (updateUserDTO.getSurname() != null) {
            user.setSurname(updateUserDTO.getSurname());
        }
        if (updateUserDTO.getUserIsAdmin() != null) {
            user.setUserIsAdmin(updateUserDTO.getUserIsAdmin());
        }
        if (updateUserDTO.getUserIsDeleted() != null) {
            user.setUserIsDeleted(updateUserDTO.getUserIsDeleted());
        }
        if (updateUserDTO.getUserIsManager() != null) {
            user.setUserIsManager(updateUserDTO.getUserIsManager());
        }
        if (updateUserDTO.getOffice() != null) {
            user.setOffice(updateUserDTO.getOffice());
        }
        if (updateUserDTO.getPhone() != null) {
            user.setPhone(updateUserDTO.getPhone());
        }
        if (updateUserDTO.getBirthdate() != null) {
            user.setBirthdate(updateUserDTO.getBirthdate());
        }
        if (updateUserDTO.getStreet() != null) {
            user.setStreet(updateUserDTO.getStreet());
        }
        if (updateUserDTO.getPostalCode() != null) {
            user.setPostalCode(updateUserDTO.getPostalCode());
        }
        if (updateUserDTO.getMunicipality() != null) {
            user.setMunicipality(updateUserDTO.getMunicipality());
        }
        if (updateUserDTO.getBiography() != null) {
            user.setBiography(updateUserDTO.getBiography());
        }
        if (updateUserDTO.getAccountState() != null) {
            user.setAccountState(updateUserDTO.getAccountState());
        }
        if (updateUserDTO.getRole() != null) {
            user.setRole(updateUserDTO.getRole());
        }
        // Chama aqui para atualizar o accountState se necessário
        checkAndUpdateAccountState(id);
        // Persist the changes
        userRepository.persist(user);
        return true;
    }

    /**
     * Retrieves a user by their unique ID and maps to a UserDTO.
     *
     * @param id The user ID
     * @return The mapped UserDTO, or null if not found
     */
    public UserDTO getUser(Long id) {
        UserEntity user = userRepository.findUserById(id);
        UserDTO userDTO = userMapper.toDto(user);
        return userDTO;
    }

    public UserDTO getUserProfile(Long id, boolean fullResponse) {
        UserEntity user = userRepository.findUserById(id);
        UserDTO userDTO = new UserDTO();
        if(fullResponse) {
            userDTO = userMapper.toFullDto(user);
        }
        else {
            userDTO = userMapper.toUserResponseDto(user);
        }
        return userDTO;
    }

    /**
     * Retrieves a paginated and filtered list of users as DTOs, with total count
     * and pagination info.
     *
     * @param id           User ID to filter (optional)
     * @param email        Email to filter (optional)
     * @param name         Name or surname to filter (optional)
     * @param phone        Phone number to filter (optional)
     * @param accountState Account state to filter (optional)
     * @param roleStr      Role to filter (optional)
     * @param office       Office to filter (optional)
     * @param parameter    Sorting parameter (optional)
     * @param orderBy      Sorting order (ASCENDING or DESCENDING)
     * @param offset       Pagination offset (start position)
     * @param limit        Pagination limit (max results)
     * @return Map containing the list of users, total count, offset, and limit
     */
    public Map<String, Object> getUsers(Long id, String email, String name, String phone,
            AccountState accountState, String roleStr, Office office,
            Boolean userIsManager, Boolean userIsAdmin, Boolean userHasManager,
            Parameter parameter, OrderBy orderBy, int offset, int limit) {

        List<UserEntity> users = userRepository.getUsers(id, email, name, phone,
                accountState, roleStr, office,
                userIsManager, userIsAdmin, userHasManager,
                parameter, orderBy, offset, limit);
        long totalUsers = userRepository.getTotalUserCount(id, email, name, phone,
                accountState, roleStr, office, userIsManager, userIsAdmin, userHasManager);
        List<UserResponseDTO> userDtos = users.stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("users", userDtos);
        responseData.put("totalUsers", totalUsers);
        responseData.put("offset", offset);
        responseData.put("limit", limit);
        return responseData;
    }

    /**
     * Generates a CSV file of all users matching the given filters and sorting (no
     * pagination).
     *
     * @param id             User ID to filter (optional)
     * @param email          Email to filter (optional)
     * @param name           Name or surname to filter (optional)
     * @param phone          Phone number to filter (optional)
     * @param accountState   Account state to filter (optional)
     * @param roleStr        Role to filter (optional)
     * @param office         Office to filter (optional)
     * @param userIsManager  Manager filter (optional)
     * @param userIsAdmin    Admin filter (optional)
     * @param userHasManager Managed filter (optional)
     * @param parameter      Sorting parameter (optional)
     * @param orderBy        Sorting order (ASCENDING or DESCENDING)
     * @return CSV file as byte array
     */
    @Transactional
    public byte[] generateUsersCSV(Long id, String email, String name, String phone,
            AccountState accountState, String roleStr, Office office,
            Boolean userIsManager, Boolean userIsAdmin, Boolean userHasManager, Language lang,
            Parameter parameter, OrderBy orderBy, boolean isAdmin) {
        try {
        LOGGER.info("Generating CSV for users with filters: id={}, email={}, name={}, phone={}, accountState={}, roleStr={}, office={}, userIsManager={}, userIsAdmin={}, userHasManager={}, parameter={}, orderBy={}",
                id, email, name, phone, accountState, roleStr, office, userIsManager, userIsAdmin, userHasManager, parameter, orderBy);
        // Fetch all users matching filters, no pagination
        List<UserEntity> users = userRepository.getUsers(id, email, name, phone,
                accountState, roleStr, office,
                userIsManager, userIsAdmin, userHasManager,
                parameter, orderBy, null, null);

        List<UserDTO> userDtos = users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());

       
            return CSVGenerator.generateUserCSV(userDtos, lang, isAdmin);
        } catch (Exception e) {
            LOGGER.error("Error generating CSV for users", e);
            throw new RuntimeException("Failed to generate CSV", e);
        }
    }

    /**
     * Generates an XLSX file of all users matching the given filters and sorting (no pagination).
     *
     * @param id             User ID to filter (optional)
     * @param email          Email to filter (optional)
     * @param name           Name or surname to filter (optional)
     * @param phone          Phone number to filter (optional)
     * @param accountState   Account state to filter (optional)
     * @param roleStr        Role to filter (optional)
     * @param office         Office to filter (optional)
     * @param userIsManager  Manager filter (optional)
     * @param userIsAdmin    Admin filter (optional)
     * @param userHasManager Managed filter (optional)
     * @param parameter      Sorting parameter (optional)
     * @param orderBy        Sorting order (ASCENDING or DESCENDING)
     * @param lang           Language for header translation
     * @param isAdmin        Whether the export is performed by an admin (affects columns)
     * @return XLSX file as byte array
     */
    @Transactional
    public byte[] generateUsersXLSX(Long id, String email, String name, String phone,
            AccountState accountState, String roleStr, Office office,
            Boolean userIsManager, Boolean userIsAdmin, Boolean userHasManager, Language lang,
            Parameter parameter, OrderBy orderBy, boolean isAdmin) {
        try {
            LOGGER.info("Generating XLSX for users with filters: id={}, email={}, name={}, phone={}, accountState={}, roleStr={}, office={}, userIsManager={}, userIsAdmin={}, userHasManager={}, parameter={}, orderBy={}",
                    id, email, name, phone, accountState, roleStr, office, userIsManager, userIsAdmin, userHasManager, parameter, orderBy);
            // Fetch all users matching filters, no pagination
            List<UserEntity> users = userRepository.getUsers(id, email, name, phone,
                    accountState, roleStr, office,
                    userIsManager, userIsAdmin, userHasManager,
                    parameter, orderBy, null, null);

            List<UserDTO> userDtos = users.stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());

            return pt.uc.dei.utils.XLSXGenerator.generateUserXLSX(userDtos, lang, isAdmin);
        } catch (Exception e) {
            LOGGER.error("Error generating XLSX for users", e);
            throw new RuntimeException("Failed to generate XLSX", e);
        }
    }

    /**
     * Deletes temporary user information, removing associated activation tokens.
     *
     * @param userToDelete The temporary user DTO to be deleted.
     * @return {@code true} if deletion was successful, {@code false} otherwise.
     */
    @Transactional
    public boolean deleteTemporaryUserInformation(TemporaryUserDTO userToDelete) {
        TemporaryUserEntity user = temporaryUserRepository.findTemporaryUserByEmail(userToDelete.getEmail());
        List<ActivationTokenEntity> activationTokens = activationTokenRepository.getTokensOfUser(user);
        // Delete activation tokens
        for (ActivationTokenEntity activationToken : activationTokens) {
            activationTokenRepository.remove(activationToken);
        }
        activationTokenRepository.flush();
        // Delete the temporary user
        deleteTemporaryUser(userToDelete);
        return true;
    }

    /**
     * Deletes a temporary user from the repository.
     *
     * @param userToDelete The temporary user DTO to be removed.
     * @return {@code true} if deletion was successful, {@code false} otherwise.
     */
    private boolean deleteTemporaryUser(TemporaryUserDTO userToDelete) {
        try {
            TemporaryUserEntity temporaryUser = temporaryUserRepository
                    .findTemporaryUserByEmail(userToDelete.getEmail());
            temporaryUserRepository.remove(temporaryUser);
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to delete temporary user: {}", userToDelete.getEmail(), e);
            return false;
        }
    }

    public boolean checkIfManagerOfUser(Long userId, Long managerId) {
        UserEntity user = userRepository.findUserById(userId);
        if (user == null) {
            LOGGER.error("User with ID {} not found", userId);
            return false;
        }
        UserEntity manager = user.getManagerUser();
        boolean isManager = manager != null && manager.getId().equals(managerId);
        LOGGER.info("User with ID {} is managed by manager with ID {}: {}", userId, managerId, isManager);
        return isManager;
    }

    public boolean checkIfUserIsAdmin(Long userId) {
        UserEntity user = userRepository.findUserById(userId);
        if (user == null) {
            LOGGER.error("User with ID {} not found", userId);
            return false;
        }
        boolean isAdmin = user.getUserIsAdmin();
        LOGGER.info("User with ID {} is admin: {}", userId, isAdmin);
        return isAdmin;
    }

    /**
     * Converts a {@link TemporaryUserEntity} to a {@link TemporaryUserDTO}.
     *
     * @param temporaryUser The temporary user entity.
     * @return A data transfer object representation of the temporary user.
     */
    public TemporaryUserDTO temporaryUserEntityToTemporaryUserDTO(TemporaryUserEntity temporaryUser) {
        TemporaryUserDTO temporaryUserDTO = new TemporaryUserDTO();
        temporaryUserDTO.setEmail(temporaryUser.getEmail());
        temporaryUserDTO.setPassword(temporaryUser.getPassword());
        temporaryUserDTO.setId(temporaryUser.getId());
        temporaryUserDTO.setSecretKey(temporaryUser.getSecretKey());
        return temporaryUserDTO;
    }

    /**
     * Checks and updates the account state of a user based on profile completeness.
     *
     * @param userId The user ID to check
     * @return true if the account state was updated or already correct, false
     *         otherwise
     */
    @Transactional
    public boolean checkAndUpdateAccountState(Long userId) {
        UserEntity user = userRepository.findUserById(userId);
        if (user == null)
            return false;

        boolean isComplete = user.getHasAvatar() &&
                user.getBiography() != null &&
                user.getBirthdate() != null &&
                user.getMunicipality() != null &&
                user.getName() != null &&
                user.getPhone() != null &&
                user.getPostalCode() != null &&
                user.getStreet() != null &&
                user.getSurname() != null;

        if (isComplete && user.getAccountState() == AccountState.INCOMPLETE) {
            user.setAccountState(AccountState.COMPLETE);
            userRepository.persist(user);
        }
        LOGGER.info("User account state updated for user ID: " + userId + ", new state: " + user.getAccountState());
        return true;
    }
    /**
     * Adds a finished course for a user. Throws exceptions if user or course not found.
     *
     * @param userId   The user ID
     * @param courseId The course ID
     * @return The created FinishedCourseEntity
     * @throws IllegalArgumentException if user or course not found
     */
    @Transactional
    public FinishedCourseDTO addFinishedCourse(Long userId, Long courseId) {
        UserEntity user = userRepository.findUserById(userId);
        if (user == null) {
            LOGGER.error("User not found with id: {}", userId);
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        CourseEntity course = courseRepository.findCourseById(courseId);
        if (course == null) {
            LOGGER.error("Course not found with id: {}", courseId);
            throw new IllegalArgumentException("Course not found with id: " + courseId);
        }
        if (course.getCourseIsActive() == null || !course.getCourseIsActive()) {
            LOGGER.error("Course is not active: {}", courseId);
            throw new IllegalArgumentException("Course is no longer active.");
        }
        // Check if already completed using user's completed courses
        if (user.getCompletedCourses() != null && user.getCompletedCourses().stream().anyMatch(fc -> fc.getCourse() != null && fc.getCourse().getId().equals(courseId))) {
            LOGGER.warn("User {} already has course {} as completed.", userId, courseId);
            throw new IllegalStateException("User already has this course as completed.");
        }
        FinishedCourseEntity finished = new FinishedCourseEntity();
        finished.setUser(user);
        finished.setCourse(course);
        finished.setCompletionDate(LocalDate.now());
        finishedCourseRepository.persist(finished);
        LOGGER.info("Added finished course: userId={}, courseId={}, date={}", userId, courseId, finished.getCompletionDate());
        return finishedCourseMapper.toDto(finished);
    }

    private boolean checkIfUserStillIsManager(Long managerId) {
        if (userRepository.checkIfUserStillHasManagedUsers(managerId)) {
            LOGGER.info("User with ID {} still manages users", managerId);
            return true;
        } else {
            LOGGER.info("User with ID {} manages no users", managerId);
            return false;
        }
    }

    private void updateManagerStatus(UserEntity user) {
        user.setUserIsManager(false);
        userRepository.persist(user);
        LOGGER.info("User with ID {} is no longer a manager", user.getId());
    }

    /**
     * Updates admin permissions for a user.
     * Only administrators can use this functionality.
     * Users cannot remove their own admin permissions.
     *
     * @param userId The ID of the user to update
     * @param isAdmin The new admin status
     * @param requesterId The ID of the user making the request
     * @return true if update was successful, false otherwise
     */
    @Transactional
    public Boolean updateAdminPermissions(Long userId, boolean isAdmin, Long requesterId) {
        try {
            // Verify requester is admin
            UserEntity requester = userRepository.findUserById(requesterId);
            if (requester == null || !requester.getUserIsAdmin()) {
                LOGGER.error("Non-admin user {} attempted to modify admin permissions", requesterId);
                return false;
            }
            
            // Prevent self-removal of admin permissions
            if (userId.equals(requesterId) && !isAdmin) {
                LOGGER.error("User {} attempted to remove their own admin permissions", requesterId);
                return false;
            }
            
            UserEntity user = userRepository.findUserById(userId);
            if (user == null) {
                LOGGER.error("User not found with ID: {}", userId);
                return false;
            }
            
            user.setUserIsAdmin(isAdmin);
            userRepository.merge(user);
            
            LOGGER.info("Admin permissions updated: User {} isAdmin={} by user {}", userId, isAdmin, requesterId);
            return true;
            
        } catch (Exception e) {
            LOGGER.error("Error updating admin permissions for user {}: {}", userId, e.getMessage());
            return false;
        }
    }
}