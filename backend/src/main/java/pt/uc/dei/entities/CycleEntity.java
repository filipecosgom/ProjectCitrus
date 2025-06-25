package pt.uc.dei.entities;

import jakarta.persistence.*;
import pt.uc.dei.enums.CycleState;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Entity representing a cycle.
 * Stores details including start and end dates, status, and administrative ownership.
 */
@Entity
@Table(name = "cycle")
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

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public CycleState getState() {
        return state;
    }

    public void setState(CycleState state) {
        this.state = state;
    }

    public UserEntity getAdmin() {
        return admin;
    }

    public void setAdmin(UserEntity admin) {
        this.admin = admin;
    }
}