package pt.uc.dei.dtos;
import jakarta.validation.constraints.NotNull;
import pt.uc.dei.enums.CycleState;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing a cycle.
 * Stores essential information related to a specific cycle.
 */
public class CycleDTO {    /**
     * The unique identifier for the cycle.
     * Auto-generated, not required for creation.
     */
    private Long id;

    /**
     * The start date and time of the cycle.
     * Must not be null.
     */
    @NotNull(message = "Start date missing")
    private LocalDate startDate;

    /**
     * The end date and time of the cycle.
     * Must not be null.
     */
    @NotNull(message = "End date missing")
    private LocalDate endDate;

    /**
     * The current state of the cycle.
     * Must not be null.
     */
    private CycleState state;

    /**
     * The ID of the administrator responsible for managing the cycle.
     */
    private Long adminId;

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

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}