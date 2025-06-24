package pt.uc.dei.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for updating an existing appraisal.
 * Contains only the fields that can be modified after creation.
 */
public class UpdateAppraisalDTO {

    /**
     * The ID of the appraisal to update.
     * Must not be null.
     */
    @NotNull(message = "Appraisal ID is required")
    private Long id;

    /**
     * The updated feedback provided by the appraising user.
     * Must not be blank.
     */
    @NotBlank(message = "Feedback is required")
    private String feedback;

    /**
     * The updated score assigned during the appraisal (1-4 stars).
     * Must be between 1 and 4.
     */
    @NotNull(message = "Score is required")
    @Min(value = 1, message = "Score must be at least 1")
    @Max(value = 4, message = "Score must be at most 4")
    private Integer score;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
