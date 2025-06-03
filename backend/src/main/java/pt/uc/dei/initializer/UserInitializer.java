package pt.uc.dei.initializer;

import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.enums.Office;
import pt.uc.dei.enums.Role;
import pt.uc.dei.repositories.UserRepository;
import pt.uc.dei.services.UserService;
import pt.uc.dei.utils.PasswordUtils;

import java.time.LocalDateTime;
/**
 * Creates the default administrator account during system startup.
 * <p>
 * Ensures there's always at least one admin user with credentials:
 * - Email: citrus.apiteam@gmail.com
 * - Password: admin (encrypted)
 *
 * @Singleton Guarantees single initialization
 */
@Singleton
public class UserInitializer {
    @EJB
    private UserRepository userRepository;
    @EJB
    private UserService userService;

    /**
     * Creates default admin account if none exists.
     * <p>
     * Sets up complete profile with administrative privileges.
     */
    public void initializeAdminUser() {
        UserEntity admin = userRepository.findUserByEmail("citrus.apiteam@gmail.com");
        if (admin == null) {
            admin = new UserEntity();
            // Core credentials
            admin.setEmail("citrus.apiteam@gmail.com");
            admin.setPassword(PasswordUtils.encrypt("admin"));

            // Profile information
            admin.setName("Rick");
            admin.setSurname("Overy");
            admin.setPhone("+351 239 859 900");
            admin.setBirthdate(LocalDateTime.of(1995, 1, 20, 0, 0));
            admin.setStreet("Paço das Escolas");
            admin.setPostalCode("3004-531");
            admin.setMunicipality("Coimbra");
            admin.setBiography("Default administrator");

            // System settings
            admin.setAdmin(true);
            admin.setDeleted(false);
            admin.setManager(false);
            admin.setOffice(Office.NO_OFFICE);
            admin.setAccountState(AccountState.COMPLETE);
            admin.setRole(Role.WITHOUT_ROLE);
            admin.setCreationDate(LocalDateTime.now());

            userRepository.persist(admin);
        }
    }
}