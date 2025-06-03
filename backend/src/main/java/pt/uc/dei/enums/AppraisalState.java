package pt.uc.dei.enums;

/**
 * Enum representing the status of an appraisal.
 * - IN_PROGRESS: The evaluation is ongoing and has not yet been completed.
 * - COMPLETED: The evaluation has been finished and results are available.
 * - CLOSED: The evaluation has been finalized and cannot be modified further.
 */
public enum AppraisalState {
    IN_PROGRESS,
    COMPLETED,
    CLOSED;
}