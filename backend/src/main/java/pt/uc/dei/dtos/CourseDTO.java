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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}