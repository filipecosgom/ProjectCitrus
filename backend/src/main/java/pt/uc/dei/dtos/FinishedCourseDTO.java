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
     */
    public FinishedCourseDTO(Long id, Long userId, String userEmail,
                             String courseTitle, String courseDescription,
                             LocalDate completionDate) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.courseTitle = courseTitle;
        this.courseDescription = courseDescription;
        this.completionDate = completionDate;
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
        if (completionDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Completion date cannot be in the future");
        }
        this.completionDate = completionDate;
    }
}