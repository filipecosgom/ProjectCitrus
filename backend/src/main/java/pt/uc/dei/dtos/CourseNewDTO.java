package pt.uc.dei.dtos;

import pt.uc.dei.enums.CourseArea;
import pt.uc.dei.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for creating a new course.
 * All fields are required except for admin (set in service).
 */
public class CourseNewDTO {
    @NotBlank(message = "Title missing")
    private String title;

    private LocalDate creationDate;

    @NotNull(message = "Duration missing")
    private Integer duration;

    @NotNull(message = "Language missing")
    private Language language;

    @NotNull(message = "Area missing")
    private CourseArea area;

    @NotBlank(message = "Description missing")
    private String description;

    @NotBlank(message = "Link missing")
    private String link;

    @NotNull(message = "CourseHasImage missing")
    private Boolean courseHasImage;

    private Boolean courseIsActive;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

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
