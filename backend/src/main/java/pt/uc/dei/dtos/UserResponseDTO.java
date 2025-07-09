package pt.uc.dei.dtos;

import jakarta.validation.constraints.NotBlank;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.enums.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class UserResponseDTO extends UserDTO {
    private Set<FinishedCourseDTO> completedCourses;

    public UserResponseDTO() {
        super();
    }

    public Set<FinishedCourseDTO> getCompletedCourses() {
        return completedCourses;
    }

    public void setCompletedCourses(Set<FinishedCourseDTO> completedCourses) {
        this.completedCourses = completedCourses;
    }
}