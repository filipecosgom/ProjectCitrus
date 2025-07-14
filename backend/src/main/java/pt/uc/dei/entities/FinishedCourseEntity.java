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
/**
 * Entity representing the completion of a course by a user.
 * <p>
 * Indexes are added to optimize queries filtering by user, course, and completion date.
 * <ul>
 *   <li>user_id: For queries like "all courses completed by a user".</li>
 *   <li>course_id: For queries like "all users who completed a course".</li>
 *   <li>completion_date: For filtering or sorting by completion date.</li>
 *   <li>Unique constraint on (user_id, course_id): Ensures a user cannot complete the same course twice and provides a unique index for this pair.</li>
 * </ul>
 */
@Entity
@Table(name = "user_course_completion",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"}),
        indexes = {
            /**
             * Index for queries like "all courses completed by a user".
             */
            @Index(name = "idx_completion_user", columnList = "user_id"),
            /**
             * Index for queries like "all users who completed a course".
             */
            @Index(name = "idx_completion_course", columnList = "course_id"),
            /**
             * Index for filtering or sorting by completion date.
             */
            @Index(name = "idx_completion_date", columnList = "completion_date")
        })
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
            name = "course_id",
            referencedColumnName = "id", // Maps to CourseEntity.id
            nullable = false
    )
    private CourseEntity course;

    /** Date when the course was completed */
    @Column(name = "completion_date", nullable = false)
    private LocalDate completionDate;

    // ===================== Getters & Setters =====================

    /**
     * Gets the unique identifier for this course completion record.
     * @return the record ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this course completion record.
     * @param id the record ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user who completed the course.
     * @return the user entity
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * Sets the user who completed the course.
     * @param user the user entity
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }

    /**
     * Gets the completed course.
     * @return the course entity
     */
    public CourseEntity getCourse() {
        return course;
    }

    /**
     * Sets the completed course.
     * @param course the course entity
     */
    public void setCourse(CourseEntity course) {
        this.course = course;
    }

    /**
     * Gets the date when the course was completed.
     * @return the completion date
     */
    public LocalDate getCompletionDate() {
        return completionDate;
    }

    /**
     * Sets the date when the course was completed.
     * @param completionDate the completion date
     */
    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }
}