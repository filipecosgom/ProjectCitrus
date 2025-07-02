package pt.uc.dei.dtos;

import jakarta.validation.constraints.NotBlank;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.enums.Role;

import java.time.LocalDateTime;

public class UserResponseDTO {
    private Long id;
    @NotBlank(message = "Email missing")
    private String email;
    private Boolean userIsAdmin;
    private Boolean userIsManager;
    private String name;
    private String surname;
    private Boolean hasAvatar;
    private AccountState accountState;
    private Role role;
    private LocalDateTime lastSeen;
    private Boolean onlineStatus;

    public UserResponseDTO() {}

    public UserResponseDTO(Long id, String email, Boolean userIsAdmin, Boolean userIsManager, AccountState accountState) {
        this.id = id;
        this.email = email;
        this.userIsAdmin = userIsAdmin;
        this.userIsManager = userIsManager;
        this.accountState = accountState;
    }

    /**
     * Retrieves the unique identifier of the user.
     * @return the user ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
     * @param id the user ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the email address of the user.
     * @return the user's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     * @param email the user's email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves whether the user is an administrator.
     * @return true if the user is an admin, false otherwise.
     */
    public Boolean getUserIsAdmin() {
        return userIsAdmin;
    }

    /**
     * Sets whether the user is an administrator.
     * @param userIsAdmin true if the user is an admin, false otherwise.
     */
    public void setUserIsAdmin(Boolean userIsAdmin) {
        this.userIsAdmin = userIsAdmin;
    }

    /**
     * Retrieves whether the user is a manager.
     * @return true if the user is a manager, false otherwise.
     */
    public Boolean getUserIsManager() {
        return userIsManager;
    }

    /**
     * Sets whether the user is a manager.
     * @param userIsManager true if the user is a manager, false otherwise.
     */
    public void setUserIsManager(Boolean userIsManager) {
        this.userIsManager = userIsManager;
    }

    /**
     * Retrieves the account state of the user.
     * @return the account state.
     */
    public AccountState getAccountState() {
        return accountState;
    }

    /**
     * Sets the account state of the user.
     * @param accountState the account state to set.
     */
    public void setAccountState(AccountState accountState) {
        this.accountState = accountState;
    }

    /**
     * Retrieves the first name of the user.
     * @return the user's first name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the first name of the user.
     * @param name the user's first name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the surname of the user.
     * @return the user's surname.
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the surname of the user.
     * @param surname the user's surname to set.
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Retrieves whether the user has an avatar.
     * @return true if the user has an avatar, false otherwise.
     */
    public Boolean getHasAvatar() {
        return hasAvatar;
    }

    /**
     * Sets whether the user has an avatar.
     * @param hasAvatar true if the user has an avatar, false otherwise.
     */
    public void setHasAvatar(Boolean hasAvatar) {
        this.hasAvatar = hasAvatar;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Boolean getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
