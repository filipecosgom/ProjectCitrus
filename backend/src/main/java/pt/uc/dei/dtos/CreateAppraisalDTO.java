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

    /**
     * Retrieves the ID of the user being appraised.
     * @return the appraised user ID.
     */
    public Long getAppraisedUserId() {
        return appraisedUserId;
    }

    /**
     * Sets the ID of the user being appraised.
     * @param appraisedUserId the appraised user ID to set.
     */
    public void setAppraisedUserId(Long appraisedUserId) {
        this.appraisedUserId = appraisedUserId;
    }

    /**
     * Retrieves the ID of the user performing the appraisal.
     * @return the appraising user ID.
     */
    public Long getAppraisingUserId() {
        return appraisingUserId;
    }

    /**
     * Sets the ID of the user performing the appraisal.
     * @param appraisingUserId the appraising user ID to set.
     */
    public void setAppraisingUserId(Long appraisingUserId) {
        this.appraisingUserId = appraisingUserId;
    }

    /**
     * Retrieves the feedback provided by the appraising user.
     * @return the feedback string.
     */
    public String getFeedback() {
        return feedback;
    }

    /**
     * Sets the feedback provided by the appraising user.
     * @param feedback the feedback string to set.
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    /**
     * Retrieves the ID of the cycle in which the appraisal occurs.
     * @return the cycle ID.
     */
    public Long getCycleId() {
        return cycleId;
    }

    /**
     * Sets the ID of the cycle in which the appraisal occurs.
     * @param cycleId the cycle ID to set.
     */
    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    /**
     * Retrieves the score assigned during the appraisal.
     * @return the appraisal score.
     */
    public Integer getScore() {
        return score;
    }

    /**
     * Sets the score assigned during the appraisal.
     * @param score the appraisal score to set.
     */
    public void setScore(Integer score) {
        this.score = score;
    }
}
