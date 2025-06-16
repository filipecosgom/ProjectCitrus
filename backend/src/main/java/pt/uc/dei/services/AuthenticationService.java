package pt.uc.dei.services;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.LoginDTO;
import pt.uc.dei.dtos.RequestAuthCodeDTO;
import pt.uc.dei.dtos.TemporaryUserDTO;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.entities.ActivationTokenEntity;
import pt.uc.dei.entities.TemporaryUserEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.mapper.UserMapper;
import pt.uc.dei.repositories.ActivationTokenRepository;
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
            UserResponseDTO userResponseDTO = userMapper.toUserResponseDto(user);
            // Generate a JWT token for authentication
            String token = jwtUtil.generateToken(userResponseDTO);
            return token;
        }
        // Return null if authentication fails (invalid credentials)
        return null;
    }

    public boolean checkAuthenticationCode(LoginDTO loginDTO) {
        // Retrieve user entity from the database using their email
        UserEntity user = findUserByEmail(loginDTO.getEmail());
        // Verify if the provided password matches the stored hash
        if (PasswordUtils.verify(user.getPassword(), loginDTO.getPassword())) {
            // Convert UserEntity to UserDTO (basic representation)
            if(TwoFactorUtil.validateCode(loginDTO.getAuthenticationCode())) {
                if (TwoFactorUtil.verifyTwoFactorCode(user.getSecretKey(), loginDTO.getAuthenticationCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    public UserResponseDTO getSelfInformation(Long id) {
        UserEntity user = userRepository.findUserById(id);
        if(user != null) {
            return userMapper.toUserResponseDto(user);
        }
        return null;
    }

    public String getAuthCode(RequestAuthCodeDTO requester) {
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
            activatedUser.setAvatar("template.png");
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

    //SUPPORT FUNCTIONS
    private UserEntity findUserByEmail(String email) {
        UserEntity user = userRepository.findUserByEmail(email);
        return user;
    }
}
