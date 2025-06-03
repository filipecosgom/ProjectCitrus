package pt.uc.dei.entities;

import jakarta.persistence.*;
import pt.uc.dei.enums.AppraisalState;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing an appraisal.
 * Stores details related to user feedback and scoring within a cycle.
 */
@Entity
@Table(name = "appraisal")
public class AppraisalEntity implements Serializable {

    /**
     * The unique identifier for the appraisal.
     * Generated automatically.
     */
    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The date and time when the appraisal was created.
     * Cannot be updated once set.
     */
    @Column(name = "creation_date", nullable = false, unique = false, updatable = false)
    private LocalDateTime creationDate;

    /**
     * The date and time when the appraisal was last edited.
     * Can be updated.
     */
    @Column(name = "edited_date", nullable = false, unique = false, updatable = true)
    private LocalDateTime editedDate;

    /**
     * The score assigned in the appraisal.
     * Optional and can be updated.
     */
    @Column(name = "score", nullable = true, unique = false, updatable = true)
    private Integer score;

    /**
     * The feedback provided during the appraisal.
     * Optional and can be updated.
     */
    @Column(name = "feedback", nullable = true, unique = false, updatable = true)
    private String feedback;

    /**
     * The current status of the appraisal.
     * Stored as a string representation of the `appraisalStatus` enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, unique = false, updatable = true)
    private AppraisalState state;

    /**
     * The cycle in which the appraisal occurs.
     * Many-to-one relationship with `CycleEntity`.
     */
    @ManyToOne
    @JoinColumn(name = "cycle_id", nullable = false, updatable = false)
    private CycleEntity cycle;

    /**
     * The user being evaluated.
     * Many-to-one relationship with `UserEntity`.
     */
    @ManyToOne
    @JoinColumn(name = "appraised_user_id", nullable = false)
    private UserEntity appraisedUser;

    /**
     * The user performing the appraisal.
     * Many-to-one relationship with `UserEntity`.
     */
    @ManyToOne
    @JoinColumn(name = "appraising_user_id", nullable = false)
    private UserEntity appraisingUser;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getEditedDate() {
        return editedDate;
    }

    public void setEditedDate(LocalDateTime editedDate) {
        this.editedDate = editedDate;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public AppraisalState getState() {
        return state;
    }

    public void setState(AppraisalState state) {
        this.state = state;
    }

    public CycleEntity getCycle() {
        return cycle;
    }

    public void setCycle(CycleEntity cycle) {
        this.cycle = cycle;
    }

    public UserEntity getAppraisedUser() {
        return appraisedUser;
    }

    public void setAppraisedUser(UserEntity evaluatedUser) {
        this.appraisedUser = evaluatedUser;
    }

    public UserEntity getAppraisingUser() {
        return appraisingUser;
    }

    public void setAppraisingUser(UserEntity evaluatingUser) {
        this.appraisingUser = evaluatingUser;
    }
}
