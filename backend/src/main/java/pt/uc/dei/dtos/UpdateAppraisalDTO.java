package pt.uc.dei.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pt.uc.dei.enums.AppraisalState;

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
    private String feedback;

    /**
     * The updated score assigned during the appraisal (1-4 stars).
     * Must be between 1 and 4.
     */
    @Min(value = 1, message = "Score must be at least 1")
    @Max(value = 4, message = "Score must be at most 4")
    private Integer score;

    private AppraisalState state;

    // Getters and Setters
    /**
     * Retrieves the ID of the appraisal to update.
     * @return the appraisal ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the appraisal to update.
     * @param id the appraisal ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the updated feedback provided by the appraising user.
     * @return the feedback string.
     */
    public String getFeedback() {
        return feedback;
    }

    /**
     * Sets the updated feedback provided by the appraising user.
     * @param feedback the feedback string to set.
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    /**
     * Retrieves the updated score assigned during the appraisal.
     * @return the appraisal score.
     */
    public Integer getScore() {
        return score;
    }

    /**
     * Sets the updated score assigned during the appraisal.
     * @param score the appraisal score to set.
     */
    public void setScore(Integer score) {
        this.score = score;
    }

    public AppraisalState getState() {
        return state;
    }

    public void setState(AppraisalState state) {
        this.state = state;
    }
}
