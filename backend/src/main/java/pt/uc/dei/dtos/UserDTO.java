package pt.uc.dei.dtos;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.enums.Office;
import pt.uc.dei.enums.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) representing a user.
 * Stores user details including authentication credentials, personal information, and role data.
 */
public class UserDTO {

    /**
     * The unique identifier for the user.
     */
    private Long id;

    /**
     * The email address of the user.
     * Must always be sent and must not be blank.
     */
    @NotBlank(message = "Email missing")
    private String email;

    /**
     * The ID of the user's manager.
     */
    private Long managerId;

    /**
     * The password of the user.
     * Must always be sent and must not be blank.
     */
    @NotBlank(message = "Password missing")
    private String password;

    /**
     * The first name of the user.
     * Must always be sent and must not be blank.
     */
    @NotBlank(message = "First name missing")
    private String name;

    /**
     * The last name of the user.
     * Must always be sent and must not be blank.
     */
    @NotBlank(message = "Last name missing")
    private String surname;

    /**
     * Indicates if the user is an administrator.
     */
    private Boolean isAdmin;

    /**
     * Indicates if the user account has been deleted.
     */
    private Boolean isDeleted;

    /**
     * Indicates if the user is a manager.
     */
    private Boolean isManager;

    /**
     * The office associated with the user.
     */
    private Office office;

    /**
     * The phone number of the user.
     * Must always be sent and must not be blank.
     */
    @NotBlank(message = "Phone missing")
    private String phone;

    /**
     * The birthdate of the user.
     */
    private LocalDateTime birthdate;

    /**
     * The user's street address.
     * Must always be sent and must not be blank.
     */
    @NotBlank(message = "Street address missing")
    private String street;

    /**
     * The postal code of the user's address.
     * Must always be sent and must not be blank.
     */
    @NotBlank(message = "Postal code missing")
    private String postalCode;

    /**
     * The municipality where the user resides.
     * Must always be sent and must not be blank.
     */
    @NotBlank(message = "Municipality missing")
    private String municipality;

    /**
     * The user's biography.
     * Must always be sent and must not be blank.
     */
    @NotBlank(message = "Biography missing")
    private String biography;

    /**
     * The account state of the user.
     */
    private AccountState accountState;

    /**
     * The role assigned to the user.
     */
    private Role role;

    /**
     * The date and time when the user account was created.
     */
    private LocalDateTime creationDate;

    /**
     * The list of evaluations the user has received.
     */
    private List<AppraisalDTO> evaluationsReceived;

    /**
     * The list of evaluations the user has given.
     */
    private List<AppraisalDTO> evaluationsGiven;

    private Set<FinishedCourseDTO> completedCourses;

    /**
     * Default constructor for `UserDTO`.
     */
    public UserDTO() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean getManager() {
        return isManager;
    }

    public void setManager(Boolean manager) {
        isManager = manager;
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

    public LocalDateTime getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDateTime birthdate) {
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public List<AppraisalDTO> getEvaluationsReceived() {
        return evaluationsReceived;
    }

    public void setEvaluationsReceived(List<AppraisalDTO> evaluationsReceived) {
        this.evaluationsReceived = evaluationsReceived;
    }

    public List<AppraisalDTO> getEvaluationsGiven() {
        return evaluationsGiven;
    }

    public void setEvaluationsGiven(List<AppraisalDTO> evaluationsGiven) {
        this.evaluationsGiven = evaluationsGiven;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Set<FinishedCourseDTO> getCompletedCourses() {
        return completedCourses;
    }

    public void setCompletedCourses(Set<FinishedCourseDTO> completedCourses) {
        this.completedCourses = completedCourses;
    }
}