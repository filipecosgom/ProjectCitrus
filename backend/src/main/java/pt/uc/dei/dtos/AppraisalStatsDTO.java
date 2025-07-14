package pt.uc.dei.dtos;

/**
 * Data Transfer Object (DTO) for appraisal statistics.
 * Stores the number of appraisals received and given by a user.
 */
public class AppraisalStatsDTO {
    /**
     * The unique identifier of the user.
     */
    private Long userId;

    /**
     * The number of appraisals received by the user.
     */
    private Long receivedAppraisalsCount;

    /**
     * The number of appraisals given by the user.
     */
    private Long givenAppraisalsCount;

    /**
     * Retrieves the user ID.
     * @return the user ID.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     * @param userId the user ID to set.
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Retrieves the number of received appraisals.
     * @return the number of received appraisals.
     */
    public Long getReceivedAppraisalsCount() {
        return receivedAppraisalsCount;
    }

    /**
     * Sets the number of received appraisals.
     * @param receivedAppraisalsCount the number to set.
     */
    public void setReceivedAppraisalsCount(Long receivedAppraisalsCount) {
        this.receivedAppraisalsCount = receivedAppraisalsCount;
    }

    /**
     * Retrieves the number of given appraisals.
     * @return the number of given appraisals.
     */
    public Long getGivenAppraisalsCount() {
        return givenAppraisalsCount;
    }

    /**
     * Sets the number of given appraisals.
     * @param givenAppraisalsCount the number to set.
     */
    public void setGivenAppraisalsCount(Long givenAppraisalsCount) {
        this.givenAppraisalsCount = givenAppraisalsCount;
    }
}
