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
import pt.uc.dei.enums.*;
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
import java.util.stream.Collectors;

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

    @Transactional
    public Boolean updateUser(Long id, UpdateUserDTO updateUserDTO) {
        // Fetch the user from the repository
        UserEntity user = userRepository.findUserById(id);
        if(user == null) {
            LOGGER.error("Update user - user not found");
            return false;
        }
        // Update only those fields that are non-null in the DTO.
        if (updateUserDTO.getManager() != null) {
            UserEntity manager = userRepository.findUserById(updateUserDTO.getManager().getId());
            if(manager != null) {
                user.setManagerUser(manager);
            }
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

    public UserDTO getUser(Long id) {
        UserEntity user = userRepository.findUserById(id);
        UserDTO userDTO = userMapper.toDto(user);
        return userDTO;
    }

    public Map<String, Object> getUsers(Long id, String email, String name, String phone,
                                        AccountState accountState, Role role, Office office,
                                        Parameter parameter, Order order, int offset, int limit) {

        List<UserEntity> users = userRepository.getUsers(id, email, name, phone,
                accountState, role, office,
                parameter, order, offset, limit);

        long totalUsers = userRepository.getTotalUserCount(id, email, name, phone,
                accountState, role, office);

        List<UserDTO> userDtos = users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("users", userDtos);
        responseData.put("totalUsers", totalUsers);
        responseData.put("offset", offset);
        responseData.put("limit", limit);
        return responseData;
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

    @Transactional
public boolean checkAndUpdateAccountState(Long userId) {
    UserEntity user = userRepository.findUserById(userId);
    if (user == null)
        return false;

    boolean isComplete =
        user.getHasAvatar() &&
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
}