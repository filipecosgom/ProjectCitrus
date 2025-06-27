package pt.uc.dei.dtos;
import jakarta.validation.constraints.NotNull;
import pt.uc.dei.enums.CycleState;

import java.time.LocalDate;
import java.util.List;

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

    @NotNull(message = "Evaluations missing")
    private List<AppraisalDTO> evaluations;


    // Getters and Setters
    /**
     * Retrieves the unique identifier for the cycle.
     * @return the cycle ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the cycle.
     * @param id the cycle ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the start date of the cycle.
     * @return the start date.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of the cycle.
     * @param startDate the start date to set.
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Retrieves the end date of the cycle.
     * @return the end date.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date of the cycle.
     * @param endDate the end date to set.
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Retrieves the current state of the cycle.
     * @return the cycle state.
     */
    public CycleState getState() {
        return state;
    }

    /**
     * Sets the current state of the cycle.
     * @param state the cycle state to set.
     */
    public void setState(CycleState state) {
        this.state = state;
    }

    /**
     * Retrieves the ID of the administrator responsible for the cycle.
     * @return the admin ID.
     */
    public Long getAdminId() {
        return adminId;
    }

    /**
     * Sets the ID of the administrator responsible for the cycle.
     * @param adminId the admin ID to set.
     */
    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public List<AppraisalDTO> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<AppraisalDTO> evaluations) {
        this.evaluations = evaluations;
    }
}