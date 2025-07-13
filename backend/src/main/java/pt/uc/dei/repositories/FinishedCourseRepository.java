package pt.uc.dei.repositories;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.ejb.Stateless;
import pt.uc.dei.entities.FinishedCourseEntity;

@Stateless
public class FinishedCourseRepository extends AbstractRepository<FinishedCourseEntity> {
    private static final Logger LOGGER = LogManager.getLogger(NotificationRepository.class);
    private static final long serialVersionUID = 1L;

    public FinishedCourseRepository() {
        super(FinishedCourseEntity.class);
    }

}