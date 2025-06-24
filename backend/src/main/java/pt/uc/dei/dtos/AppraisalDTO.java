package pt.uc.dei.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pt.uc.dei.enums.AppraisalState;

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
    private LocalDateTime creationDate;

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

    public AppraisalState getState() {
        return state;
    }

    public void setState(AppraisalState state) {
        this.state = state;
    }

    public Long getCycleId() {
        return cycleId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }


    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

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
}