package pt.uc.dei.enums;

/**
 * Enum representing the status of an appraisal.
 * <ul>
 *   <li>IN_PROGRESS: The evaluation is ongoing and has not yet been completed.</li>
 *   <li>COMPLETED: The evaluation has been finished and results are available.</li>
 *   <li>CLOSED: The evaluation has been finalized and cannot be modified further.</li>
 * </ul>
 */
public enum AppraisalState {
    IN_PROGRESS,
    COMPLETED,
    CLOSED
}