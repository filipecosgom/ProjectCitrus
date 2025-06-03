package pt.uc.dei.entities;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Entity representing the completion of a course by a user.
 * <p>
 * - Stores user-course completion records in the database.
 * - Ensures uniqueness via a composite key (`user_id`, `course_title`).
 * - Maintains relationships with `UserEntity` and `CourseEntity`.
 * </p>
 */
@Entity
@Table(name = "user_course_completion",
        uniqueConstraints = @UniqueConstraint( // Ensures unique records per user-course pair
                columnNames = {"user_id", "course_title"}
        ))
public class FinishedCourseEntity implements Serializable {

    /** Unique identifier for each course completion record */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Reference to the user who completed the course */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Foreign key to UserEntity
    private UserEntity user;

    /** Reference to the completed course */
    @ManyToOne
    @JoinColumn(
            name = "course_title",
            referencedColumnName = "title", // Maps to CourseEntity.title
            nullable = false
    )
    private CourseEntity course;

    /** Date when the course was completed */
    @Column(name = "completion_date", nullable = false)
    private LocalDate completionDate;

    // ===================== Getters & Setters =====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public CourseEntity getCourse() {
        return course;
    }

    public void setCourse(CourseEntity course) {
        this.course = course;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }
}