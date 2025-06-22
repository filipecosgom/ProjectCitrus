package pt.uc.dei.dtos;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.enums.Office;
import pt.uc.dei.enums.Role;

import java.time.LocalDate;
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
    private ManagerDTO manager;

    /**
     * The password of the user.
     * Must always be sent and must not be blank.
     */
    @NotBlank(message = "Password missing")
    private String password;

    private Boolean hasAvatar;

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
    private Boolean userIsAdmin;

    /**
     * Indicates if the user account has been deleted.
     */
    private Boolean userIsDeleted;

    /**
     * Indicates if the user is a manager.
     */
    private Boolean userIsManager;

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
    private LocalDate birthdate;

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

    /**
     * Construtor for basic UserDTO
     * @return
     */
    public UserDTO(String email, Boolean userIsAdmin, Boolean userIsManager) {
        this.email = email;
        this.userIsAdmin = userIsAdmin;
        this.userIsManager = userIsManager;
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

    public ManagerDTO getManager() {
        return manager;
    }

    public void setManager(ManagerDTO manager) {
        this.manager = manager;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Set<FinishedCourseDTO> getCompletedCourses() {
        return completedCourses;
    }

    public void setCompletedCourses(Set<FinishedCourseDTO> completedCourses) {
        this.completedCourses = completedCourses;
    }
}