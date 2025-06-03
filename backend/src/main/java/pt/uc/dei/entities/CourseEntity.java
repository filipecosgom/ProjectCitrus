package pt.uc.dei.entities;

import jakarta.persistence.*;
import pt.uc.dei.enums.CourseArea;
import pt.uc.dei.enums.Language;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a course.
 * Stores course details including title, duration, language, area, and administrative information.
 */
@Entity
@Table(name = "course")
public class CourseEntity implements Serializable {

    /**
     * The unique title of the course.
     * Must always be sent and cannot be updated.
     */
    @Id
    @Column(name = "title", nullable = false, unique = true, updatable = false)
    private String title;

    /**
     * The date and time when the course was created.
     * Cannot be updated once set.
     */
    @Column(name = "creation_date", nullable = false, unique = false, updatable = false)
    private LocalDateTime creationDate;

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
    @Column(name = "image_link", nullable = false, unique = true, updatable = true)
    private String imageLink;

    /**
     * Indicates whether the course is active.
     * Must always be sent and can be updated.
     */
    @Column(name = "is_active", nullable = false, unique = false, updatable = true)
    private Boolean isActive;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public CourseArea getArea() {
        return area;
    }

    public void setArea(CourseArea area) {
        this.area = area;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public UserEntity getAdmin() {
        return admin;
    }

    public void setAdmin(UserEntity admin) {
        this.admin = admin;
    }
}