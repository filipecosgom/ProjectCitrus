package pt.uc.dei.dtos;

import pt.uc.dei.enums.Role;

public class ManagerDTO {
    private Long id;
    private String name;
    private String surname;
    private Role role;
    private Boolean hasAvatar;
    private String email;

    public ManagerDTO() {
    }

    public ManagerDTO(Long id, String name, String surname, Role role, Boolean hasAvatar, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.hasAvatar = hasAvatar;
        this.email = email;
    }

    /**
     * Retrieves the unique identifier of the manager.
     * @return the manager ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the manager.
     * @param id the manager ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the name of the manager.
     * @return the manager's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the manager.
     * @param name the manager's name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the surname of the manager.
     * @return the manager's surname.
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the surname of the manager.
     * @param surname the manager's surname to set.
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Retrieves the role of the manager.
     * @return the manager's role.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the role of the manager.
     * @param role the manager's role to set.
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Retrieves whether the manager has an avatar.
     * @return true if the manager has an avatar, false otherwise.
     */
    public Boolean getHasAvatar() {
        return hasAvatar;
    }

    /**
     * Sets whether the manager has an avatar.
     * @param hasAvatar true if the manager has an avatar, false otherwise.
     */
    public void setHasAvatar(Boolean hasAvatar) {
        this.hasAvatar = hasAvatar;
    }

    /**
     * Retrieves the email address of the manager.
     * @return the manager's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the manager.
     * @param email the manager's email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
