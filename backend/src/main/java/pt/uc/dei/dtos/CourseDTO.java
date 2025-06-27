package pt.uc.dei.dtos;

import jakarta.validation.constraints.NotBlank;
import pt.uc.dei.enums.CourseArea;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import pt.uc.dei.enums.Language;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a course.
 * Encapsulates essential details required for course creation and management.
 */
public class CourseDTO {
    /**
     * The title of the course.
     * Must not be blank.
     */
    @NotBlank(message = "Title missing")
    private String title;

    /**
     * The date and time when the course was created.
     */
    private LocalDateTime creationDate;

    /**
     * The duration of the course (in hours).
     * Must not be blank.
     */
    @NotBlank(message = "Duration missing")
    private Integer duration;

    /**
     * The language in which the course is offered.
     * Must not be blank.
     */
    @NotBlank(message = "Language missing")
    private Language language;

    /**
     * The subject area of the course.
     */
    private CourseArea area;

    /**
     * A brief description of the course.
     * Must not be blank.
     */
    @NotBlank(message = "Description missing")
    private String description;

    /**
     * The URL link to access the course.
     * Must not be blank.
     */
    @NotBlank(message = "Link missing")
    private String link;

    /**
     * The URL of the image representing the course.
     * Must not be blank.
     */
    @NotBlank(message = "Image link missing")
    private String imageLink;

    /**
     * Indicates whether the course is active.
     */
    private Boolean isActive;

    // Getters and Setters
    /**
     * Retrieves the title of the course.
     * @return the course title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the course.
     * @param title the course title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retrieves the date and time when the course was created.
     * @return the creation date and time.
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the date and time when the course was created.
     * @param creationDate the creation date and time to set.
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Retrieves the duration of the course (in hours).
     * @return the course duration.
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * Sets the duration of the course (in hours).
     * @param duration the course duration to set.
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * Retrieves the language in which the course is offered.
     * @return the course language.
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Sets the language in which the course is offered.
     * @param language the course language to set.
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * Retrieves whether the course is active.
     * @return true if the course is active, false otherwise.
     */
    public Boolean getActive() {
        return isActive;
    }

    /**
     * Sets whether the course is active.
     * @param active true to set the course as active, false otherwise.
     */
    public void setActive(Boolean active) {
        isActive = active;
    }

    /**
     * Retrieves the subject area of the course.
     * @return the course area.
     */
    public CourseArea getArea() {
        return area;
    }

    /**
     * Sets the subject area of the course.
     * @param area the course area to set.
     */
    public void setArea(CourseArea area) {
        this.area = area;
    }

    /**
     * Retrieves the description of the course.
     * @return the course description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the course.
     * @param description the course description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the URL link to access the course.
     * @return the course link.
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the URL link to access the course.
     * @param link the course link to set.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Retrieves the URL of the image representing the course.
     * @return the image link.
     */
    public String getImageLink() {
        return imageLink;
    }

    /**
     * Sets the URL of the image representing the course.
     * @param imageLink the image link to set.
     */
    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    /**
     * Retrieves whether the course is active.
     * @return true if the course is active, false otherwise.
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Sets whether the course is active.
     * @param isActive true to set the course as active, false otherwise.
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}