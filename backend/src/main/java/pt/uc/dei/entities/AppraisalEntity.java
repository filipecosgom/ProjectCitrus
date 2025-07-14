package pt.uc.dei.entities;

import jakarta.persistence.*;
import pt.uc.dei.enums.AppraisalState;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * JPA entity representing an appraisal record.
 * <p>
 * Stores details related to user feedback, scoring, and cycle participation.
 * Each appraisal is associated with an appraised user, an appraising user (manager), and a cycle.
 * <ul>
 *   <li><b>id</b>: Unique identifier, auto-generated.</li>
 *   <li><b>creationDate</b>: Date when the appraisal was created (immutable).</li>
 *   <li><b>editedDate</b>: Date when the appraisal was last edited.</li>
 *   <li><b>submissionDate</b>: Date when the appraisal was submitted (optional).</li>
 *   <li><b>score</b>: Score assigned in the appraisal (optional).</li>
 *   <li><b>feedback</b>: Feedback provided during the appraisal (optional).</li>
 *   <li><b>appraisedUser</b>: The user being appraised.</li>
 *   <li><b>appraisingUser</b>: The user performing the appraisal (manager).</li>
 *   <li><b>cycle</b>: The cycle in which this appraisal occurs.</li>
 *   <li><b>state</b>: The current state of the appraisal (e.g., IN_PROGRESS, COMPLETED).</li>
 * </ul>
 * <b>Indexes:</b> See class-level Javadoc for index recommendations and usage.
 */
/**
 * JPA entity representing an appraisal record.
 * <p>
 * Index recommendations:
 * <ul>
 *   <li><b>idx_appraisal_appraised_user_id</b>: For fast lookup of appraisals by the appraised user.</li>
 *   <li><b>idx_appraisal_appraising_user_id</b>: For fast lookup of appraisals by the appraising user (manager).</li>
 *   <li><b>idx_appraisal_cycle_id</b>: For efficient retrieval of appraisals within a cycle.</li>
 *   <li><b>idx_appraisal_state</b>: For filtering appraisals by their state (e.g., COMPLETED, IN_PROGRESS).</li>
 *   <li><b>idx_appraisal_appraised_appraising_cycle</b>: Composite index for efficient duplicate checks and advanced multi-field filtering.</li>
 * </ul>
 */
@Entity
@Table(name = "appraisal",
    indexes = {
        /**
         * For fast lookup of appraisals by the appraised user.
         */
        @Index(name = "idx_appraisal_appraised_user_id", columnList = "appraised_user_id"),
        /**
         * For fast lookup of appraisals by the appraising user (manager).
         */
        @Index(name = "idx_appraisal_appraising_user_id", columnList = "appraising_user_id"),
        /**
         * For efficient retrieval of appraisals within a cycle.
         */
        @Index(name = "idx_appraisal_cycle_id", columnList = "cycle_id"),
        /**
         * For filtering appraisals by their state (e.g., COMPLETED, IN_PROGRESS).
         */
        @Index(name = "idx_appraisal_state", columnList = "state"),
        /**
         * Composite index for efficient duplicate checks and advanced multi-field filtering.
         * Covers (appraised_user_id, appraising_user_id, cycle_id).
         */
        @Index(name = "idx_appraisal_appraised_appraising_cycle", columnList = "appraised_user_id,appraising_user_id,cycle_id")
    }
)
public class AppraisalEntity implements Serializable {

    /**
     * The unique identifier for the appraisal.
     * <p>
     * Generated automatically by the database.
     */
    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The date when the appraisal was created.
     * <p>
     * Cannot be updated once set. Used for sorting and filtering.
     */
    @Column(name = "creation_date", nullable = false, unique = false, updatable = false)
    private LocalDate creationDate;

    /**
     * The date when the appraisal was last edited.
     * <p>
     * Can be updated to reflect the latest modification.
     */
    @Column(name = "edited_date", nullable = false, unique = false, updatable = true)
    private LocalDate editedDate;

    /**
     * The date when the appraisal was submitted.
     * <p>
     * Optional. Used to track when the appraisal was finalized.
     */
    @Column(name = "submission_date")
    private LocalDate submissionDate;

    /**
     * The score assigned in the appraisal.
     * <p>
     * Optional and can be updated. Used for performance evaluation.
     */
    @Column(name = "score", nullable = true, unique = false, updatable = true)
    private Integer score;

    /**
     * The feedback provided during the appraisal.
     * <p>
     * Optional and can be updated. Used for qualitative assessment.
     */
    @Column(name = "feedback", nullable = true, unique = false, updatable = true)
    private String feedback;

    /**
     * The user being appraised.
     * <p>
     * Many-to-one relationship with UserEntity. Indexed for fast lookup.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appraised_user_id", nullable = false)
    private UserEntity appraisedUser;

    /**
     * The user performing the appraisal (manager).
     * <p>
     * Many-to-one relationship with UserEntity. Indexed for fast lookup.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appraising_user_id", nullable = false)
    private UserEntity appraisingUser;

    /**
     * The cycle in which this appraisal occurs.
     * <p>
     * Many-to-one relationship with CycleEntity. Indexed for efficient retrieval.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cycle_id", nullable = false)
    private CycleEntity cycle;

    /**
     * The current state of the appraisal (e.g., IN_PROGRESS, COMPLETED).
     * <p>
     * Used for filtering and workflow management. Indexed for fast filtering.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private AppraisalState state;

    // Default constructor
    public AppraisalEntity() {
        this.creationDate = LocalDate.now();
        this.editedDate = LocalDate.now();
        this.state = AppraisalState.IN_PROGRESS;
    }

    /**
     * Retrieves the unique identifier for the appraisal.
     *
     * @return the appraisal ID.
     */
    public Long getId() { return id; }

    /**
     * Sets the unique identifier for the appraisal.
     *
     * @param id the appraisal ID to set.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retrieves the creation date of the appraisal.
     *
     * @return the creation date.
     */
    public LocalDate getCreationDate() { return creationDate; }

    /**
     * Sets the creation date of the appraisal.
     *
     * @param creationDate the creation date to set.
     */
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    /**
     * Retrieves the last edited date of the appraisal.
     *
     * @return the last edited date.
     */
    public LocalDate getEditedDate() { return editedDate; }

    /**
     * Sets the last edited date of the appraisal.
     *
     * @param editedDate the last edited date to set.
     */
    public void setEditedDate(LocalDate editedDate) { this.editedDate = editedDate; }

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

    /**
     * Retrieves the submission date of the appraisal.
     *
     * @return the submission date, or null if not submitted.
     */
    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    /**
     * Sets the submission date of the appraisal.
     *
     * @param submissionDate the submission date to set.
     */
    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }
}
