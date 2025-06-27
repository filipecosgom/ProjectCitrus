package pt.uc.dei.dtos;

/**
 * Inner class for appraisal statistics.
 */
public class AppraisalStatsDTO {
    private Long userId;
    private Long receivedAppraisalsCount;
    private Long givenAppraisalsCount;

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getReceivedAppraisalsCount() {
        return receivedAppraisalsCount;
    }

    public void setReceivedAppraisalsCount(Long receivedAppraisalsCount) {
        this.receivedAppraisalsCount = receivedAppraisalsCount;
    }

    public Long getGivenAppraisalsCount() {
        return givenAppraisalsCount;
    }

    public void setGivenAppraisalsCount(Long givenAppraisalsCount) {
        this.givenAppraisalsCount = givenAppraisalsCount;
    }
}
