package pt.uc.dei.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO for assigning a manager to multiple users.
 */
public class AssignManagerRequestDTO {

    /**
     * ID of the user who will become the manager.
     */
    @NotNull(message = "Manager ID is required")
    private Long managerId;

    /**
     * List of user IDs that will be assigned to the manager.
     */
    @NotEmpty(message = "User IDs list cannot be empty")
    private List<Long> userIds;

    /**
     * Default constructor.
     */
    public AssignManagerRequestDTO() {
    }

    /**
     * Constructor with parameters.
     */
    public AssignManagerRequestDTO(Long managerId, List<Long> userIds) {
        this.managerId = managerId;
        this.userIds = userIds;
    }

    /**
     * Gets the manager ID.
     * @return the manager ID
     */
    public Long getManagerId() {
        return managerId;
    }

    /**
     * Sets the manager ID.
     * @param managerId the manager ID
     */
    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    /**
     * Gets the list of user IDs.
     * @return the list of user IDs
     */
    public List<Long> getUserIds() {
        return userIds;
    }

    /**
     * Sets the list of user IDs.
     * @param userIds the list of user IDs
     */
    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    @Override
    public String toString() {
        return "AssignManagerRequestDTO{" +
                "managerId=" + managerId +
                ", userIds=" + userIds +
                '}';
    }
}