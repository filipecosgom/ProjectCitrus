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

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getHasAvatar() {
        return hasAvatar;
    }

    public void setHasAvatar(Boolean hasAvatar) {
        this.hasAvatar = hasAvatar;
    }
}
