package pt.uc.dei.services;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.*;
import pt.uc.dei.utils.JWTUtil;
import pt.uc.dei.entities.ActivationTokenEntity;
import pt.uc.dei.entities.TemporaryUserEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.mapper.UserMapper;
import pt.uc.dei.repositories.ActivationTokenRepository;
import pt.uc.dei.repositories.TemporaryUserRepository;
import pt.uc.dei.repositories.UserRepository;
import pt.uc.dei.utils.PasswordUtils;
import pt.uc.dei.utils.TwoFactorUtil;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing user-related operations.
 * <p>
 * Provides functionality for user verification, registration, activation,
 * and temporary user management. Utilizes {@link UserRepository},
 * {@link TemporaryUserRepository}, and {@link ActivationTokenRepository} for persistence operations.
 *
 * @Stateless Marks this class as a stateless EJB, making it eligible for dependency injection
 * and transaction management by the EJB container.
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

    @Inject
    JWTUtil jwtUtil;

    @Inject
    UserMapper userMapper;

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
     * Searches both permanent and temporary repositories to verify whether the email is registered.
     *
     * @param email The email address to check for existence.
     * @return {@code true} if a user (permanent or temporary) exists with the email, {@code false} otherwise.
     * @throws jakarta.persistence.PersistenceException If an error occurs during database operations.
     */
    public boolean findIfUserExists(String email) {
        UserEntity user = userRepository.findUserByEmail(email);
        TemporaryUserEntity temporaryUser = temporaryUserRepository.findTemporaryUserByEmail(email);
        return (temporaryUser != null || user != null);
    }


    /**
     * Handles user login by validating credentials and generating a JWT authentication token.
     * <p>
     * - Retrieves the user from the database based on the provided email.
     * - Verifies the provided password against the stored hashed password using BCrypt.
     * - Converts the UserEntity into a UserDTO with basic data.
     * - Generates a JWT token upon successful authentication.
     * </p>
     *
     * @param loginDTO The login request containing the user's email and password.
     * @return A JWT token if authentication is successful; otherwise, returns {@code null}.
     */
    public String loginUser(LoginDTO loginDTO) {
        // Retrieve user entity from the database using their email
        UserEntity user = findUserByEmail(loginDTO.getEmail());
        if(user != null) {
            // Verify if the provided password matches the stored hash
            if (PasswordUtils.verify(user.getPassword(), loginDTO.getPassword())) {
                // Convert UserEntity to UserDTO (basic representation)
                if(TwoFactorUtil.verifyTwoFactorCode(user.getSecretKey(), loginDTO.getAuthenticationCode())) {
                    UserResponseDTO userResponseDTO = userMapper.toUserResponseDto(user);
                    // Generate a JWT token for authentication
                    String token = jwtUtil.generateToken(userResponseDTO);
                    return token;
                }
            }
        }
        // Return null if authentication fails (invalid credentials)
        return null;
    }

    public UserResponseDTO getSelfInformation(String email) {
        UserEntity user = userRepository.findUserByEmail(email);
        if(user != null) {
            return userMapper.toUserResponseDto(user);
        }
        return null;
    }

    public String getAuthCode(LoginDTO requester) {
        // Retrieve user entity from the database using their email
        UserEntity user = findUserByEmail(requester.getEmail());
        if(user != null) {
            // Verify if the provided password matches the stored hash
            if (PasswordUtils.verify(user.getPassword(), requester.getPassword())) {
                String authenticationCode = user.getSecretKey();
                return authenticationCode;
            }
        }
        // Return null if authentication fails (invalid credentials)
        return null;
    }

    /**
     * Registers a new temporary user in the system.
     * <p>
     * Generates an activation token and persists the temporary user in the repository.
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

        LOGGER.info("New user created with email {} and activation token {}", newUser.getEmail(), token.getTokenValue());
        return codes;
    }

    /**
     * Activates a temporary user, converting them into a permanent user.
     *
     * @param userToActivate The temporary user DTO to be activated.
     * @return {@code true} if activation was successful, {@code false} otherwise.
     */
    public boolean activateUser(TemporaryUserDTO userToActivate) {
        try {
            UserEntity activatedUser = new UserEntity();
            activatedUser.setEmail(userToActivate.getEmail());
            activatedUser.setPassword(userToActivate.getPassword());
            activatedUser.setManager(false);
            activatedUser.setAccountState(AccountState.INCOMPLETE);
            activatedUser.setCreationDate(LocalDateTime.now());
            activatedUser.setSecretKey(userToActivate.getSecretKey());
            userRepository.persist(activatedUser);
            LOGGER.info("New activated user: {}", activatedUser.getEmail());
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to activate user: {}", userToActivate.getEmail(), e);
            return false;
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
            TemporaryUserEntity temporaryUser = temporaryUserRepository.findTemporaryUserByEmail(userToDelete.getEmail());
            temporaryUserRepository.remove(temporaryUser);
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to delete temporary user: {}", userToDelete.getEmail(), e);
            return false;
        }
    }

    public UserResponseDTO getResponseUserByEmail(String email) {
        UserEntity user = userRepository.findUserByEmail(email);
        return userMapper.toUserResponseDto(user);
    }



    //SUPPORT FUNCTIONS
    private UserEntity findUserByEmail(String email) {
        UserEntity user = userRepository.findUserByEmail(email);
        return user;
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
}