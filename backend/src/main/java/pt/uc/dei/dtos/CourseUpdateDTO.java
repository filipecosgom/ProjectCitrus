package pt.uc.dei.dtos;

import pt.uc.dei.enums.CourseArea;
import pt.uc.dei.enums.Language;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for updating a course.
 * All fields are optional; only provided fields will be updated.
 */
public class CourseUpdateDTO {
    private Long id;
    private String title;
    private LocalDateTime creationDate;
    private Integer duration;
    private Language language;
    private CourseArea area;
    private String description;
    private String link;
    private Boolean courseHasImage;
    private Boolean courseIsActive;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }

    public CourseArea getArea() { return area; }
    public void setArea(CourseArea area) { this.area = area; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public Boolean getCourseHasImage() { return courseHasImage; }
    public void setCourseHasImage(Boolean courseHasImage) { this.courseHasImage = courseHasImage; }

    public Boolean getCourseIsActive() { return courseIsActive; }
    public void setCourseIsActive(Boolean courseIsActive) { this.courseIsActive = courseIsActive; }
}
