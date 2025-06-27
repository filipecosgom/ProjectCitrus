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

    /**
     * Retrieves the unique identifier for the user.
     * @return the user ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the user.
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
     * Retrieves the manager of the user.
     * @return the manager DTO.
     */
    public ManagerDTO getManager() {
        return manager;
    }

    /**
     * Sets the manager of the user.
     * @param manager the manager DTO to set.
     */
    public void setManager(ManagerDTO manager) {
        this.manager = manager;
    }

    /**
     * Retrieves the password of the user.
     * @return the user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     * @param password the user's password to set.
     */
    public void setPassword(String password) {
        this.password = password;
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
     * Retrieves the last name of the user.
     * @return the user's last name.
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the last name of the user.
     * @param surname the user's last name to set.
     */
    public void setSurname(String surname) {
        this.surname = surname;
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
     * Retrieves whether the user account has been deleted.
     * @return true if the user is deleted, false otherwise.
     */
    public Boolean getUserIsDeleted() {
        return userIsDeleted;
    }

    /**
     * Sets whether the user account has been deleted.
     * @param userIsDeleted true if the user is deleted, false otherwise.
     */
    public void setUserIsDeleted(Boolean userIsDeleted) {
        this.userIsDeleted = userIsDeleted;
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
     * Retrieves the office associated with the user.
     * @return the office.
     */
    public Office getOffice() {
        return office;
    }

    /**
     * Sets the office associated with the user.
     * @param office the office to set.
     */
    public void setOffice(Office office) {
        this.office = office;
    }

    /**
     * Retrieves the phone number of the user.
     * @return the user's phone number.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the user.
     * @param phone the user's phone number to set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Retrieves the birthdate of the user.
     * @return the user's birthdate.
     */
    public LocalDate getBirthdate() {
        return birthdate;
    }

    /**
     * Sets the birthdate of the user.
     * @param birthdate the user's birthdate to set.
     */
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * Retrieves the street address of the user.
     * @return the user's street address.
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the street address of the user.
     * @param street the user's street address to set.
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Retrieves the postal code of the user's address.
     * @return the user's postal code.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postal code of the user's address.
     * @param postalCode the user's postal code to set.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Retrieves the municipality where the user resides.
     * @return the user's municipality.
     */
    public String getMunicipality() {
        return municipality;
    }

    /**
     * Sets the municipality where the user resides.
     * @param municipality the user's municipality to set.
     */
    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    /**
     * Retrieves the user's biography.
     * @return the user's biography.
     */
    public String getBiography() {
        return biography;
    }

    /**
     * Sets the user's biography.
     * @param biography the user's biography to set.
     */
    public void setBiography(String biography) {
        this.biography = biography;
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
     * Retrieves the role assigned to the user.
     * @return the user's role.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the role assigned to the user.
     * @param role the user's role to set.
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Retrieves the date and time when the user account was created.
     * @return the creation date and time.
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the date and time when the user account was created.
     * @param creationDate the creation date and time to set.
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Retrieves the list of evaluations the user has received.
     * @return the list of received evaluations.
     */
    public List<AppraisalDTO> getEvaluationsReceived() {
        return evaluationsReceived;
    }

    /**
     * Sets the list of evaluations the user has received.
     * @param evaluationsReceived the list of received evaluations to set.
     */
    public void setEvaluationsReceived(List<AppraisalDTO> evaluationsReceived) {
        this.evaluationsReceived = evaluationsReceived;
    }

    /**
     * Retrieves the list of evaluations the user has given.
     * @return the list of given evaluations.
     */
    public List<AppraisalDTO> getEvaluationsGiven() {
        return evaluationsGiven;
    }

    /**
     * Sets the list of evaluations the user has given.
     * @param evaluationsGiven the list of given evaluations to set.
     */
    public void setEvaluationsGiven(List<AppraisalDTO> evaluationsGiven) {
        this.evaluationsGiven = evaluationsGiven;
    }

    /**
     * Retrieves the set of completed courses for the user.
     * @return the set of completed courses.
     */
    public Set<FinishedCourseDTO> getCompletedCourses() {
        return completedCourses;
    }

    /**
     * Sets the set of completed courses for the user.
     * @param completedCourses the set of completed courses to set.
     */
    public void setCompletedCourses(Set<FinishedCourseDTO> completedCourses) {
        this.completedCourses = completedCourses;
    }
}