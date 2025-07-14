package pt.uc.dei.entities;

import jakarta.persistence.*;
import pt.uc.dei.enums.CourseArea;
import pt.uc.dei.enums.Language;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a course in the system.
 * <p>
 * Stores course details including title, duration, language, area, description, link, image, active status, and administrative information.
 * <p>
 * <b>Indexes:</b>
 * <ul>
 *   <li>area: For area-based listings and filtering.</li>
 *   <li>admin_id: For filtering courses managed by a specific admin.</li>
 *   <li>is_active: For filtering by active/inactive status.</li>
 *   <li>creation_date: For sorting/filtering by creation date.</li>
 *   <li>area, is_active: Composite index for efficient queries filtering by area and active status together.</li>
 * </ul>
 */
@Entity
@Table(name = "course",
       indexes = {
           /**
            * Index for filtering courses by area (e.g., for area-based listings).
            */
           @Index(name = "idx_course_area", columnList = "area"),
           /**
            * Index for filtering courses by admin (e.g., for admin's managed courses).
            */
           @Index(name = "idx_course_admin", columnList = "admin_id"),
           /**
            * Index for filtering by active/inactive status.
            */
           @Index(name = "idx_course_is_active", columnList = "is_active"),
           /**
            * Index for sorting/filtering by creation date (e.g., recent courses).
            */
           @Index(name = "idx_course_creation_date", columnList = "creation_date"),
           /**
            * Composite index for efficient queries filtering by area and active status together.
            */
           @Index(name = "idx_course_area_is_active", columnList = "area, is_active")
       })
public class CourseEntity implements Serializable {

    /**
     * The unique identifier for the course.
     * Generated automatically.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    /**
     * The unique title of the course.
     * Must always be sent and can be updated.
     */
    @Column(name = "title", nullable = false, unique = true, updatable = true)
    private String title;

    /**
     * The date and time when the course was created.
     * Cannot be updated once set.
     */
    @Column(name = "creation_date", nullable = false, unique = false, updatable = false)
    private LocalDate creationDate;

    /**
     * The duration of the course (in hours).
     * Must always be sent and can be updated.
     */
    @Column(name = "duration", nullable = false, unique = false, updatable = true)
    private Integer duration;

    /**
     * The language in which the course is offered.
     * Stored as a string representation of the `Language` enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false, unique = false, updatable = true)
    private Language language;

    /**
     * The subject area of the course.
     * Stored as a string representation of the `CourseArea` enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "area", nullable = false, unique = false, updatable = true)
    private CourseArea area;

    /**
     * A brief description of the course.
     * Must always be sent and can be updated.
     */
    @Column(name = "description", nullable = false, unique = false, updatable = true)
    private String description;

    /**
     * The URL link to access the course.
     * Must be unique and can be updated.
     */
    @Column(name = "link", nullable = false, unique = true, updatable = true)
    private String link;

    /**
     * The URL of the image representing the course.
     * Must be unique and can be updated.
     */
    @Column(name = "image", nullable = false, unique = false, updatable = true)
    private Boolean courseHasImage;

    /**
     * Indicates whether the course is active.
     * Must always be sent and can be updated.
     */
    @Column(name = "is_active", nullable = false, unique = false, updatable = true)
    private Boolean courseIsActive;

    /**
     * The administrator responsible for managing the course.
     * Many-to-one relationship with `UserEntity`.
     */
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false, updatable = false)
    private UserEntity admin;

    @OneToMany(mappedBy = "course")
    private Set<FinishedCourseEntity> userCompletions = new HashSet<>();

    // Getters and Setters

    /**
     * Gets the unique identifier of the course.
     * @return the course id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the course.
     * @param id the course id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the unique title of the course.
     * @return the course title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the unique title of the course.
     * @param title the course title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the creation date and time of the course.
     * @return the creation date
     */
    public LocalDate getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date and time of the course.
     * @param creationDate the creation date
     */
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets the duration of the course in hours.
     * @return the course duration
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * Sets the duration of the course in hours.
     * @param duration the course duration
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * Gets the language in which the course is offered.
     * @return the course language
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Sets the language in which the course is offered.
     * @param language the course language
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * Gets the subject area of the course.
     * @return the course area
     */
    public CourseArea getArea() {
        return area;
    }

    /**
     * Sets the subject area of the course.
     * @param area the course area
     */
    public void setArea(CourseArea area) {
        this.area = area;
    }

    /**
     * Gets the description of the course.
     * @return the course description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the course.
     * @param description the course description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the URL link to access the course.
     * @return the course link
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the URL link to access the course.
     * @param link the course link
     */
    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getCourseHasImage() {
        return courseHasImage;
    }

    public void setCourseHasImage(Boolean courseHasImage) {
        this.courseHasImage = courseHasImage;
    }

    public Set<FinishedCourseEntity> getUserCompletions() {
        return userCompletions;
    }

    public void setUserCompletions(Set<FinishedCourseEntity> userCompletions) {
        this.userCompletions = userCompletions;
    }

    public Boolean getCourseIsActive() {
        return courseIsActive;
    }

    public void setCourseIsActive(Boolean courseIsActive) {
        this.courseIsActive = courseIsActive;
    }

    /**
     * Gets the administrator responsible for managing the course.
     * @return the admin user entity
     */
    public UserEntity getAdmin() {
        return admin;
    }

    /**
     * Sets the administrator responsible for managing the course.
     * @param admin the admin user entity
     */
    public void setAdmin(UserEntity admin) {
        this.admin = admin;
    }
}