package pt.uc.dei.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pt.uc.dei.enums.AppraisalState;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing an appraisal.
 * Stores information related to user feedback and scoring within a cycle.
 */
public class AppraisalDTO {    /**
     * The unique identifier for the appraisal.
     * Auto-generated, not required for creation.
     */
    private Long id;

    /**
     * The ID of the user being appraised.
     * Must not be null.
     */
    @NotNull
    private Long appraisedUserId;

    /**
     * The ID of the user performing the appraisal.
     * Must not be null.
     */
    @NotNull
    private Long appraisingUserId;

    /**
     * The feedback provided by the appraising user.
     * Must not be blank.
     */
    @NotBlank(message = "Feedback missing")
    private String feedback;

    /**
     * The ID of the cycle in which the appraisal occurs.
     */
    private Long cycleId;

    /**
     * The score assigned during the appraisal.
     */
    private Integer score;

    /**
     * The current status of the appraisal.
     */
    private AppraisalState state;

    /**
     * The date and time when the appraisal was created.
     */
    private LocalDate creationDate;

    private LocalDate submissionDate;

    // Getters and Setters
    /**
     * Retrieves the unique identifier for the appraisal.
     * @return the appraisal ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the appraisal.
     * @param id the appraisal ID to set.
     */
    public void setId(Long id) {
        this.id = id;
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

    /**
     * Retrieves the current status of the appraisal.
     * @return the appraisal state.
     */
    public AppraisalState getState() {
        return state;
    }

    /**
     * Sets the current status of the appraisal.
     * @param state the appraisal state to set.
     */
    public void setState(AppraisalState state) {
        this.state = state;
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
     * Retrieves the date and time when the appraisal was created.
     * @return the creation date and time.
     */
    public LocalDate getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the date and time when the appraisal was created.
     * @param creationDate the creation date and time to set.
     */
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

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

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }
}