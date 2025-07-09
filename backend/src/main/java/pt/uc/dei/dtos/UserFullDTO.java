package pt.uc.dei.dtos;

import java.util.List;
import java.util.Set;

public class UserFullDTO extends UserDTO {
    private List<AppraisalDTO> evaluationsReceived;
    private List<AppraisalDTO> evaluationsGiven;
    private Set<FinishedCourseDTO> completedCourses;

    public UserFullDTO() {
        super();
    }

    public List<AppraisalDTO> getEvaluationsReceived() {
        return evaluationsReceived;
    }

    public void setEvaluationsReceived(List<AppraisalDTO> evaluationsReceived) {
        this.evaluationsReceived = evaluationsReceived;
    }

    public List<AppraisalDTO> getEvaluationsGiven() {
        return evaluationsGiven;
    }

    public void setEvaluationsGiven(List<AppraisalDTO> evaluationsGiven) {
        this.evaluationsGiven = evaluationsGiven;
    }

    public Set<FinishedCourseDTO> getCompletedCourses() {
        return completedCourses;
    }

    public void setCompletedCourses(Set<FinishedCourseDTO> completedCourses) {
        this.completedCourses = completedCourses;
    }
}
