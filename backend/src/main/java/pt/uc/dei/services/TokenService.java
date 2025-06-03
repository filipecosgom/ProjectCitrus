package pt.uc.dei.services;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.ActivationTokenDTO;
import pt.uc.dei.dtos.PasswordResetTokenDTO;
import pt.uc.dei.dtos.TemporaryUserDTO;
import pt.uc.dei.entities.ActivationTokenEntity;
import pt.uc.dei.entities.ConfigurationEntity;
import pt.uc.dei.entities.TemporaryUserEntity;
import pt.uc.dei.repositories.ActivationTokenRepository;
import pt.uc.dei.repositories.ConfigurationRepository;
import pt.uc.dei.repositories.TemporaryUserRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Service class for handling activation token generation and management.
 * <p>
 * This stateless EJB provides functionality for creating secure activation tokens
 * used for user account verification processes.
 *
 * @Stateless Marks this class as a stateless EJB, making it eligible for
 * dependency injection and transaction management by the EJB container.
 */
@Stateless
public class TokenService {
    /**
     * Logger instance for logging operations within this class.
     * Note: The logger is initialized with UserService.class which might be a typo.
     */
    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    /**
     * Serial version UID for serialization support.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Injected repository for activation token persistence operations.
     */
    @EJB
    ActivationTokenRepository activationTokenRepository;

    /**
     * Injected repository for temporary user persistence operations.
     */
    @EJB
    TemporaryUserRepository temporaryUserRepository;

    @EJB
    ConfigurationRepository configurationRepository;

    @EJB
    UserService userService;

    /**
     * Creates and persists a new activation token for a temporary user.
     * <p>
     * This method:
     * <ol>
     *   <li>Finds the temporary user by email</li>
     *   <li>Generates a secure random token</li>
     *   <li>Sets creation timestamp</li>
     *   <li>Associates the token with the user</li>
     *   <li>Persists the token</li>
     * </ol>
     *
     * @param newUser The DTO containing temporary user information
     * @return The generated token value as a Base64-encoded string
     * @throws jakarta.persistence.PersistenceException If an error occurs during persistence
     * @throws IllegalArgumentException                 If the temporary user cannot be found
     * @see TemporaryUserDTO
     * @see ActivationTokenEntity
     */
    public String createNewActivationToken(TemporaryUserDTO newUser) {
        TemporaryUserEntity temporaryUser = temporaryUserRepository.findTemporaryUserByEmail(newUser.getEmail());
        ActivationTokenEntity activationToken = new ActivationTokenEntity();
        activationToken.setTokenValue(generateNewToken());
        activationToken.setCreationDate(LocalDateTime.now());
        activationToken.setTemporaryUser(temporaryUser);
        activationTokenRepository.persist(activationToken);
        return activationToken.getTokenValue();
    }

    /**
     * Renews an existing token by deleting the old one and generating a new activation token.
     * <p>
     * Currently supports activation tokens, with future extensions planned for password reset tokens.
     *
     * @param user  The user associated with the token renewal.
     * @param token The token to be renewed.
     * @return A new activation token if renewal is successful, {@code null} otherwise.
     */
    public String renewToken(Object user, Object token) {
        if (token instanceof ActivationTokenDTO) {
            deleteToken(token);
            return createNewActivationToken((TemporaryUserDTO) user);
        }
        // Future implementation for renewing password reset tokens.
        return null;
    }

    /**
     * Deletes an existing activation token.
     * <p>
     * Removes the activation token from the associated temporary user and deletes it from the repository.
     *
     * @param token The token to be deleted.
     * @return {@code true} if the token was successfully deleted, {@code false} otherwise.
     */
    public boolean deleteToken(Object token) {
        if (token instanceof ActivationTokenDTO) {
            ActivationTokenEntity activationToken = activationTokenRepository.getTokenFromValue(((ActivationTokenDTO) token).getTokenValue());
            TemporaryUserEntity user = activationToken.getTemporaryUser();
            user.setActivationToken(null);
            temporaryUserRepository.merge(user);
            activationTokenRepository.remove(activationToken);
            activationTokenRepository.flush();
            return true;
        }
        return false;
    }

    /**
     * Retrieves the temporary user associated with a given activation token.
     *
     * @param activationTokenDTO The activation token data transfer object.
     * @return The corresponding {@link TemporaryUserDTO} if found, {@code null} otherwise.
     */
    public TemporaryUserDTO getTemporaryUserFromActivationToken(ActivationTokenDTO activationTokenDTO) {
        if (activationTokenDTO.getTokenValue() == null) {
            return null;
        } else {
            ActivationTokenEntity activationToken = activationTokenRepository.getTokenFromValue(activationTokenDTO.getTokenValue());
            if (activationToken == null) {
                return null;
            }
            TemporaryUserEntity userToActivate = temporaryUserRepository.findTemporaryUserByEmail(activationToken.getTemporaryUser().getEmail());
            return userService.temporaryUserEntityToTemporaryUserDTO(userToActivate);
        }
    }

    /**
     * Retrieves an activation token entity based on the provided activation token value.
     *
     * @param activationTokenDTO The activation token data transfer object.
     * @return A corresponding {@link ActivationTokenDTO} if found, {@code null} otherwise.
     */
    public ActivationTokenDTO getActivationTokenByValue(ActivationTokenDTO activationTokenDTO) {
        ActivationTokenEntity activationToken = activationTokenRepository.getTokenFromValue(activationTokenDTO.getTokenValue());
        return activationTokenEntityToActivationTokenDTO(activationToken);
    }

    /**
     * Checks whether a given token has expired.
     *
     * @param token The token to verify.
     * @return {@code true} if the token is expired, {@code false} otherwise.
     */
    public boolean isTokenExpired(Object token) {
        try {
            LocalDateTime expirationDate = getExpirationDate(token);
            if (expirationDate == null) {
                LOGGER.error("Expiration date is null for token type: " + token.getClass().getName());
                return false;
            }
            return LocalDateTime.now().isAfter(expirationDate);
        } catch (Exception e) {
            LOGGER.error("Error checking token expiration: ", e);
            return false;
        }
    }

    /**
     * Determines the expiration date of a given token based on the latest system configuration.
     *
     * @param token The token for which the expiration date is being determined.
     * @return The expiration {@link LocalDateTime}, or {@code null} if unable to determine.
     */
    private LocalDateTime getExpirationDate(Object token) {
        ConfigurationEntity latestConfiguration = configurationRepository.getLatestConfiguration();
        if (token instanceof ActivationTokenDTO) {
            return ((ActivationTokenDTO) token).getCreationDate().plusMinutes(latestConfiguration.getVerificationTime());
        }
        if (token instanceof PasswordResetTokenDTO) {
            return ((PasswordResetTokenDTO) token).getCreationDate().plusMinutes(latestConfiguration.getVerificationTime());
        }
        return null;
    }

    /**
     * Converts an {@link ActivationTokenEntity} to an {@link ActivationTokenDTO}.
     *
     * @param activationTokenEntity The activation token entity.
     * @return A data transfer object representation of the activation token.
     */
    private ActivationTokenDTO activationTokenEntityToActivationTokenDTO(ActivationTokenEntity activationTokenEntity) {
        ActivationTokenDTO activationTokenDTO = new ActivationTokenDTO();
        activationTokenDTO.setTokenValue(activationTokenEntity.getTokenValue());
        activationTokenDTO.setCreationDate(activationTokenEntity.getCreationDate());
        return activationTokenDTO;
    }

    /**
     * Generates a new secure random token.
     * <p>
     * Creates a cryptographically strong random token using:
     * <ul>
     *   <li>SecureRandom for random number generation</li>
     *   <li>24 bytes of random data</li>
     *   <li>Base64 URL-safe encoding</li>
     * </ul>
     *
     * @return A URL-safe Base64-encoded random token string
     * @implNote This implementation is thread-safe as both SecureRandom
     * and Base64.Encoder instances used are thread-safe.
     */
    public String generateNewToken() {
        SecureRandom secureRandom = new SecureRandom(); //threadsafe
        Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}