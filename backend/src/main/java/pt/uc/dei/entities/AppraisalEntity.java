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
     * The user being appraised.
     * Many-to-one relationship with UserEntity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appraised_user_id", nullable = false)
    private UserEntity appraisedUser;

    /**
     * The user performing the appraisal (manager).
     * Many-to-one relationship with UserEntity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appraising_user_id", nullable = false)
    private UserEntity appraisingUser;

    /**
     * The cycle in which this appraisal occurs.
     * Many-to-one relationship with CycleEntity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cycle_id", nullable = false)
    private CycleEntity cycle;

    /**
     * The current state of the appraisal.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private AppraisalState state;

    // Construtor padrão
    public AppraisalEntity() {
        this.creationDate = LocalDateTime.now();
        this.editedDate = LocalDateTime.now();
        this.state = AppraisalState.IN_PROGRESS;
    }

    /**
     * Retrieves the unique identifier for the appraisal.
     * @return the appraisal ID.
     */
    public Long getId() { return id; }

    /**
     * Sets the unique identifier for the appraisal.
     * @param id the appraisal ID to set.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retrieves the creation date of the appraisal.
     * @return the creation date and time.
     */
    public LocalDateTime getCreationDate() { return creationDate; }

    /**
     * Sets the creation date of the appraisal.
     * @param creationDate the creation date and time to set.
     */
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    /**
     * Retrieves the last edited date of the appraisal.
     * @return the last edited date and time.
     */
    public LocalDateTime getEditedDate() { return editedDate; }

    /**
     * Sets the last edited date of the appraisal.
     * @param editedDate the last edited date and time to set.
     */
    public void setEditedDate(LocalDateTime editedDate) { this.editedDate = editedDate; }

    /**
     * Retrieves the score assigned in the appraisal.
     * @return the appraisal score.
     */
    public Integer getScore() { return score; }

    /**
     * Sets the score assigned in the appraisal.
     * @param score the appraisal score to set.
     */
    public void setScore(Integer score) { this.score = score; }

    /**
     * Retrieves the feedback provided during the appraisal.
     * @return the feedback string.
     */
    public String getFeedback() { return feedback; }

    /**
     * Sets the feedback provided during the appraisal.
     * @param feedback the feedback string to set.
     */
    public void setFeedback(String feedback) { this.feedback = feedback; }

    /**
     * Retrieves the current state of the appraisal.
     * @return the appraisal state.
     */
    public AppraisalState getState() { return state; }

    /**
     * Sets the current state of the appraisal.
     * @param state the appraisal state to set.
     */
    public void setState(AppraisalState state) { this.state = state; }

    /**
     * Retrieves the user being appraised.
     * @return the appraised user entity.
     */
    public UserEntity getAppraisedUser() { return appraisedUser; }

    /**
     * Sets the user being appraised.
     * @param appraisedUser the appraised user entity to set.
     */
    public void setAppraisedUser(UserEntity appraisedUser) { this.appraisedUser = appraisedUser; }

    /**
     * Retrieves the user performing the appraisal.
     * @return the appraising user entity.
     */
    public UserEntity getAppraisingUser() { return appraisingUser; }

    /**
     * Sets the user performing the appraisal.
     * @param appraisingUser the appraising user entity to set.
     */
    public void setAppraisingUser(UserEntity appraisingUser) { this.appraisingUser = appraisingUser; }

    /**
     * Retrieves the cycle in which this appraisal occurs.
     * @return the cycle entity.
     */
    public CycleEntity getCycle() { return cycle; }

    /**
     * Sets the cycle in which this appraisal occurs.
     * @param cycle the cycle entity to set.
     */
    public void setCycle(CycleEntity cycle) { this.cycle = cycle; }
}
