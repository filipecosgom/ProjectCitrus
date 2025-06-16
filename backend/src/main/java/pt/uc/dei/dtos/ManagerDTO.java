package pt.uc.dei.dtos;

import pt.uc.dei.enums.Role;

public class ManagerDTO {
    private Long id;
    private String name;
    private String surname;
    private Role role;
    private String avatar;

    public ManagerDTO() {
    }

    public ManagerDTO(Long id, String name, String surname, Role role, String avatar) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.avatar = avatar;
    }

    public Long getId() {
        return id;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
