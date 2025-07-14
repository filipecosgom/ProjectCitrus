package pt.uc.dei.entities;

import jakarta.persistence.*;
import pt.uc.dei.enums.CycleState;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a cycle.
 * Stores details including start and end dates, status, and administrative ownership.
 */
/**
 * Entity representing a cycle.
 * <p>
 * Indexes are added to optimize queries filtering by state, admin, start and end dates, and for efficient date range and state-based queries.
 * <ul>
 *   <li>state: For filtering by cycle state (OPEN, CLOSED, etc.).</li>
 *   <li>admin_id: For filtering cycles managed by a specific admin.</li>
 *   <li>start_date: For ordering and filtering by start date.</li>
 *   <li>end_date: For filtering by end date and expired cycles.</li>
 *   <li>state, start_date: Composite index for queries filtering by state and ordering by start date.</li>
 *   <li>start_date, end_date: Composite index for date range overlap queries.</li>
 * </ul>
 */
@Entity
@Table(name = "cycle",
       indexes = {
           /**
            * Index for filtering by cycle state (OPEN, CLOSED, etc.).
            */
           @Index(name = "idx_cycle_state", columnList = "state"),
           /**
            * Index for filtering cycles managed by a specific admin.
            */
           @Index(name = "idx_cycle_admin", columnList = "admin_id"),
           /**
            * Index for ordering and filtering by start date.
            */
           @Index(name = "idx_cycle_start_date", columnList = "start_date"),
           /**
            * Index for filtering by end date and expired cycles.
            */
           @Index(name = "idx_cycle_end_date", columnList = "end_date"),
           /**
            * Composite index for queries filtering by state and ordering by start date.
            */
           @Index(name = "idx_cycle_state_start_date", columnList = "state, start_date"),
           /**
            * Composite index for date range overlap queries.
            */
           @Index(name = "idx_cycle_start_end_date", columnList = "start_date, end_date")
       })
public class CycleEntity implements Serializable {

    /**
     * The unique identifier for the cycle.
     * Generated automatically.
     */
    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The start date and time of the cycle.
     * Must always be set and can be updated.
     */
    @Column(name = "start_date", nullable = false, unique = false, updatable = true)
    private LocalDate startDate;

    /**
     * The end date and time of the cycle.
     * Must always be set and can be updated.
     */
    @Column(name = "end_date", nullable = false, unique = false, updatable = true)
    private LocalDate endDate;

    /**
     * The current status of the cycle.
     * Stored as a string representation of the `CycleStatus` enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, unique = false, updatable = true)
    private CycleState state;

    /**
     * The administrator responsible for managing the cycle.
     * Many-to-one relationship with `UserEntity`.
     */
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false, updatable = false)
    private UserEntity admin;

    @OneToMany(mappedBy = "cycle", fetch = FetchType.LAZY)
    private List<AppraisalEntity> evaluations;


    // Getters and Setters

    /**
     * Retrieves the unique identifier for the cycle.
     * @return the cycle ID.
     */
    public Long getId() { return id; }

    /**
     * Sets the unique identifier for the cycle.
     * @param id the cycle ID to set.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retrieves the start date of the cycle.
     * @return the start date.
     */
    public LocalDate getStartDate() { return startDate; }

    /**
     * Sets the start date of the cycle.
     * @param startDate the start date to set.
     */
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    /**
     * Retrieves the end date of the cycle.
     * @return the end date.
     */
    public LocalDate getEndDate() { return endDate; }

    /**
     * Sets the end date of the cycle.
     * @param endDate the end date to set.
     */
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    /**
     * Retrieves the current status of the cycle.
     * @return the cycle state.
     */
    public CycleState getState() { return state; }

    /**
     * Sets the current status of the cycle.
     * @param state the cycle state to set.
     */
    public void setState(CycleState state) { this.state = state; }

    /**
     * Retrieves the administrator responsible for managing the cycle.
     * @return the admin user entity.
     */
    public UserEntity getAdmin() { return admin; }

    /**
     * Sets the administrator responsible for managing the cycle.
     * @param admin the admin user entity to set.
     */
    public void setAdmin(UserEntity admin) { this.admin = admin; }

    /**
     * Retrieves the list of evaluations (appraisals) associated with this cycle.
     * @return the list of evaluations for this cycle
     */
    public List<AppraisalEntity> getEvaluations() {
        return evaluations;
    }

    /**
     * Sets the list of evaluations (appraisals) for this cycle.
     * @param evaluations the list of evaluations to set
     */
    public void setEvaluations(List<AppraisalEntity> evaluations) {
        this.evaluations = evaluations;
    }
}