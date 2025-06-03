package pt.uc.dei.dtos;
import jakarta.validation.constraints.NotNull;
import pt.uc.dei.enums.CycleState;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a cycle.
 * Stores essential information related to a specific cycle.
 */
public class CycleDTO {

    /**
     * The unique identifier for the cycle.
     * Must always be sent.
     */
    @NotNull(message = "Cycle ID missing")
    private Long id;

    /**
     * The start date and time of the cycle.
     * Must not be null.
     */
    @NotNull(message = "Start date missing")
    private LocalDateTime startDate;

    /**
     * The end date and time of the cycle.
     * Must not be null.
     */
    @NotNull(message = "End date missing")
    private LocalDateTime endDate;

    /**
     * The current status of the cycle.
     * Must not be null.
     */
    @NotNull(message = "Status missing")
    private CycleState status;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public CycleState getStatus() {
        return status;
    }

    public void setStatus(CycleState status) {
        this.status = status;
    }
}