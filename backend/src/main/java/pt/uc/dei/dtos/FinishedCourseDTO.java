package pt.uc.dei.dtos;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing a completed course by a user.
 * <p>
 * Used to transfer completion records between layers without exposing entity details.
 */
public class FinishedCourseDTO {
    /** Unique identifier of the completion record */
    private Long id;

    /** ID of the user who completed the course */
    private Long userId;

    /** Email of the user (for display purposes) */
    private String userEmail;

    /** Title of the completed course */
    private String courseTitle;

    /** Description of the course (from CourseEntity) */
    private String courseDescription;

    /** Date when the course was completed */
    private LocalDate completionDate;

    /** ID of the course (from CourseEntity) */
    private Long courseId;

    /** Area of the course */
    private String courseArea;

    /** Duration of the course (in hours) */
    private Integer courseDuration;

    /** Language of the course */
    private String courseLanguage;

    /** Link to the course */
    private String courseLink;

    /** Whether the course has an image */
    private Boolean courseHasImage;

    /** Whether the course is active */
    private Boolean courseIsActive;

    private LocalDate courseCreationDate;

    /**
     * Default constructor.
     */
    public FinishedCourseDTO() {}

    /**
     * All-arguments constructor.
     *
     * @param id Unique identifier of the record
     * @param userId ID of the user who completed the course
     * @param userEmail Email address of the user
     * @param courseTitle Title of the completed course
     * @param courseDescription Description of the course
     * @param completionDate Date of completion (ISO format, e.g. 2023-12-31)
     * @param courseId ID of the course
     * @param courseArea Area of the course
     * @param courseDuration Duration of the course
     * @param courseLanguage Language of the course
     * @param courseLink Link to the course
     * @param courseHasImage Whether the course has an image
     * @param courseIsActive Whether the course is active
     */
    public FinishedCourseDTO(Long id, Long userId, String userEmail,
                             String courseTitle, String courseDescription,
                             LocalDate completionDate, Long courseId,
                             String courseArea, Integer courseDuration,
                             String courseLanguage, String courseLink,
                             Boolean courseHasImage, Boolean courseIsActive) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.courseTitle = courseTitle;
        this.courseDescription = courseDescription;
        this.completionDate = completionDate;
        this.courseId = courseId;
        this.courseArea = courseArea;
        this.courseDuration = courseDuration;
        this.courseLanguage = courseLanguage;
        this.courseLink = courseLink;
        this.courseHasImage = courseHasImage;
        this.courseIsActive = courseIsActive;
    }

    // ============== GETTERS AND SETTERS ==============

    /**
     * @return The unique database identifier of this completion record
     */
    public Long getId() { return id; }

    /**
     * @param id The unique identifier to set
     */
    public void setId(Long id) { this.id = id; }

    /**
     * @return The ID of the user who completed the course
     */
    public Long getUserId() { return userId; }

    /**
     * @param userId The user ID to set
     */
    public void setUserId(Long userId) { this.userId = userId; }

    /**
     * @return The email address of the user who completed the course
     */
    public String getUserEmail() { return userEmail; }

    /**
     * @param userEmail The email address to set
     */
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    /**
     * @return The title of the completed course
     */
    public String getCourseTitle() { return courseTitle; }

    /**
     * @param courseTitle The course title to set
     */
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    /**
     * @return The description of the completed course
     */
    public String getCourseDescription() { return courseDescription; }

    /**
     * @param courseDescription The course description to set
     */
    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    /**
     * @return The date when the course was completed (ISO-8601 format)
     */
    public LocalDate getCompletionDate() { return completionDate; }

    /**
     * @param completionDate The completion date to set
     * @throws IllegalArgumentException if date is in the future
     */
    public void setCompletionDate(LocalDate completionDate) {
        if (completionDate != null && completionDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Completion date cannot be in the future");
        }
        this.completionDate = completionDate;
    }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseArea() { return courseArea; }
    public void setCourseArea(String courseArea) { this.courseArea = courseArea; }

    public Integer getCourseDuration() { return courseDuration; }
    public void setCourseDuration(Integer courseDuration) { this.courseDuration = courseDuration; }

    public String getCourseLanguage() { return courseLanguage; }
    public void setCourseLanguage(String courseLanguage) { this.courseLanguage = courseLanguage; }

    public String getCourseLink() { return courseLink; }
    public void setCourseLink(String courseLink) { this.courseLink = courseLink; }

    public Boolean getCourseHasImage() { return courseHasImage; }
    public void setCourseHasImage(Boolean courseHasImage) { this.courseHasImage = courseHasImage; }

    public Boolean getCourseIsActive() { return courseIsActive; }
    public void setCourseIsActive(Boolean courseIsActive) { this.courseIsActive = courseIsActive; }

    public LocalDate getCourseCreationDate() {
        return courseCreationDate;
    }

    public void setCourseCreationDate(LocalDate courseCreationDate) {
        this.courseCreationDate = courseCreationDate;
    }
}