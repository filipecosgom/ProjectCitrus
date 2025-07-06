package pt.uc.dei.dtos;

import pt.uc.dei.enums.AccountState;
import pt.uc.dei.enums.Office;
import pt.uc.dei.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UpdateUserDTO {

    private Long userId;

    // The user's manager (if updating manager information)
    private Long managerId;

    // The user's avatar URL or identifier.
    private Boolean hasAvatar;

    // The user's first name.
    private String name;

    // The user's last name.
    private String surname;

    // Flags regarding the user's admin/manager/deleted status.
    private Boolean userIsAdmin;
    private Boolean userIsDeleted;
    private Boolean userIsManager;

    // The office associated with the user.
    private Office office;

    // The user's phone number.
    private String phone;

    // The user's birthdate.
    private LocalDate birthdate;

    // The user's street address.
    private String street;

    // The user's postal code.
    private String postalCode;

    // The municipality where the user resides.
    private String municipality;

    // The user's biography.
    private String biography;

    // The account state of the user.
    private AccountState accountState;

    // The role assigned to the user.
    private Role role;

    // Default no-args constructor.
    public UpdateUserDTO() {
    }

    // Getters and Setters
    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Boolean getHasAvatar() {
        return hasAvatar;
    }

    public void setHasAvatar(Boolean hasAvatar) {
        this.hasAvatar = hasAvatar;
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

    public Boolean getUserIsAdmin() {
        return userIsAdmin;
    }

    public void setUserIsAdmin(Boolean userIsAdmin) {
        this.userIsAdmin = userIsAdmin;
    }

    public Boolean getUserIsDeleted() {
        return userIsDeleted;
    }

    public void setUserIsDeleted(Boolean userIsDeleted) {
        this.userIsDeleted = userIsDeleted;
    }

    public Boolean getUserIsManager() {
        return userIsManager;
    }

    public void setUserIsManager(Boolean userIsManager) {
        this.userIsManager = userIsManager;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public AccountState getAccountState() {
        return accountState;
    }

    public void setAccountState(AccountState accountState) {
        this.accountState = accountState;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}