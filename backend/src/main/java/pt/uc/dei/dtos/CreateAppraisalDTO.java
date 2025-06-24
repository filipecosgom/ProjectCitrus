package pt.uc.dei.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for creating a new appraisal.
 * Contains only the fields necessary for appraisal creation.
 */
public class CreateAppraisalDTO {

    /**
     * The ID of the user being appraised.
     * Must not be null.
     */
    @NotNull(message = "Appraised user ID is required")
    private Long appraisedUserId;

    /**
     * The ID of the user performing the appraisal.
     * Must not be null.
     */
    @NotNull(message = "Appraising user ID is required")
    private Long appraisingUserId;

    /**
     * The feedback provided by the appraising user.
     * Must not be blank.
     */
    @NotBlank(message = "Feedback is required")
    private String feedback;

    /**
     * The ID of the cycle in which the appraisal occurs.
     * Must not be null.
     */
    @NotNull(message = "Cycle ID is required")
    private Long cycleId;

    /**
     * The score assigned during the appraisal (1-4 stars).
     * Must be between 1 and 4.
     */
    @NotNull(message = "Score is required")
    @Min(value = 1, message = "Score must be at least 1")
    @Max(value = 4, message = "Score must be at most 4")
    private Integer score;

    // Getters and Setters
    public Long getAppraisedUserId() {
        return appraisedUserId;
    }

    public void setAppraisedUserId(Long appraisedUserId) {
        this.appraisedUserId = appraisedUserId;
    }

    public Long getAppraisingUserId() {
        return appraisingUserId;
    }

    public void setAppraisingUserId(Long appraisingUserId) {
        this.appraisingUserId = appraisingUserId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Long getCycleId() {
        return cycleId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
