package pt.uc.dei.services;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.*;
import pt.uc.dei.entities.ActivationTokenEntity;
import pt.uc.dei.entities.PasswordResetTokenEntity;
import pt.uc.dei.entities.TemporaryUserEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.mapper.UserMapper;
import pt.uc.dei.repositories.ActivationTokenRepository;
import pt.uc.dei.repositories.PasswordResetTokenRepository;
import pt.uc.dei.repositories.TemporaryUserRepository;
import pt.uc.dei.repositories.UserRepository;
import pt.uc.dei.utils.JWTUtil;
import pt.uc.dei.utils.PasswordUtils;
import pt.uc.dei.utils.TwoFactorUtil;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class AuthenticationService implements Serializable {
    /**
     * Logger instance for logging user service operations.
     */
    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    /**
     * Serial version UID for ensuring class consistency during serialization.
     */
    private static final long serialVersionUID = 1L;

    @Inject
    UserRepository userRepository;


    @Inject
    JWTUtil jwtUtil;

    @Inject
    UserMapper userMapper;


    /**
     * Injected repository for activation token persistence.
     */
    @EJB
    ActivationTokenRepository activationTokenRepository;

    @EJB
    PasswordResetTokenRepository passwordResetTokenRepository;


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
    @Transactional
    public String loginUser(LoginDTO loginDTO) {
        // Retrieve user entity from the database using their email
        UserEntity user = findUserByEmail(loginDTO.getEmail());
        if (user != null) {
            if (PasswordUtils.verify(user.getPassword(), loginDTO.getPassword())) {
                UserResponseDTO userResponseDTO = userMapper.toUserResponseDto(user);
                // Generate a JWT token for authentication
                String token = jwtUtil.generateToken(userResponseDTO);
                setUserOnline(user.getId());
                return token;
            }
        }
        // Return null if authentication fails (invalid credentials)
        return null;
    }

    @Transactional
    public boolean logoutUser(Long id) {
        if(setUserOffline(id)) {
            return true;
        }
        return false;
    }

    /**
     * Checks the authentication code for a user during login.
     *
     * @param loginDTO The login request containing the user's email and authentication code
     * @return true if the authentication code is valid, false otherwise
     */
    public boolean checkAuthenticationCode(LoginDTO loginDTO) {
        // Retrieve user entity from the database using their email
        UserEntity user = findUserByEmail(loginDTO.getEmail());
        // Convert UserEntity to UserDTO (basic representation)
        if (TwoFactorUtil.validateCode(loginDTO.getAuthenticationCode())) {
            if (TwoFactorUtil.verifyTwoFactorCode(user.getSecretKey(), loginDTO.getAuthenticationCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the authenticated user's information as a UserResponseDTO.
     *
     * @param id The user ID
     * @return The UserResponseDTO if found, null otherwise
     */
    public UserResponseDTO getSelfInformation(Long id) {
        UserEntity user = userRepository.findUserById(id);
        if (user != null) {
            return userMapper.toUserResponseDto(user);
        }
        return null;
    }

    /**
     * Retrieves the authentication code (secret key) for a user if credentials are valid.
     *
     * @param requester The DTO containing the user's email and password
     * @return The authentication code (secret key) if credentials are valid, null otherwise
     */
    public String getAuthCode(RequestAuthCodeDTO requester) {
        // Retrieve user entity from the database using their email
        UserEntity user = findUserByEmail(requester.getEmail());
        if (user != null) {
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
            activatedUser.setUserIsManager(false);
            activatedUser.setHasAvatar(false);
            activatedUser.setAccountState(AccountState.INCOMPLETE);
            activatedUser.setCreationDate(LocalDateTime.now());
            activatedUser.setSecretKey(userToActivate.getSecretKey());
            activatedUser.setOnlineStatus(false);
            activatedUser.setLastSeen(LocalDateTime.now());
            userRepository.persist(activatedUser);
            LOGGER.info("New activated user: {}", activatedUser.getEmail());
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to activate user: {}", userToActivate.getEmail(), e);
            return false;
        }
    }

    /**
     * Sets a new password for a user using a valid password reset token.
     *
     * @param passwordResetTokenDTO The DTO containing the password reset token
     * @param newPassword           The new password to set
     * @return true if the password was successfully set, false otherwise
     */
    @Transactional
    public boolean setNewPassword(PasswordResetTokenDTO passwordResetTokenDTO, String newPassword) {
        try {
            PasswordResetTokenEntity passwordResetToken = passwordResetTokenRepository.getTokenFromValue(passwordResetTokenDTO.getTokenValue());
            UserEntity user = userRepository.findUserById(passwordResetToken.getUser().getId());
            user.setPassword(PasswordUtils.encrypt(newPassword));
            userRepository.persist(user);
            passwordResetTokenRepository.remove(passwordResetToken);
            return true;
        } catch (Exception e) {
            LOGGER.info("Password setting for token {}", passwordResetTokenDTO.getTokenValue());
            return false;
        }
    }

    private UserEntity findUserByEmail(String email) {
        UserEntity user = userRepository.findUserByEmail(email);
        return user;
    }

    public boolean setUserOnline(Long userId) {
    UserEntity user = userRepository.findUserById(userId);
        if (user != null) {
            user.setOnlineStatus(true);
            user.setLastSeen(LocalDateTime.now());
            userRepository.merge(user);
            return true;
        }
        return false;
    }

    public boolean setUserOffline(Long userId) {
        UserEntity user = userRepository.findUserById(userId);
        if (user != null) {
            user.setOnlineStatus(false);
            user.setLastSeen(LocalDateTime.now());
            userRepository.merge(user);
            return true;
        }
        return false;
    }
}
