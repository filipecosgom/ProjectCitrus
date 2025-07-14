package pt.uc.dei.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.uc.dei.enums.CycleState;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CycleEntityTest {
    private CycleEntity cycle;
    private UserEntity admin;

    @BeforeEach
    void setUp() {
        cycle = new CycleEntity();
        admin = new UserEntity();
        cycle.setId(1L);
        cycle.setStartDate(LocalDate.of(2025, 1, 1));
        cycle.setEndDate(LocalDate.of(2025, 12, 31));
        cycle.setState(CycleState.OPEN);
        cycle.setAdmin(admin);
        List<AppraisalEntity> evaluations = new ArrayList<>();
        cycle.setEvaluations(evaluations);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, cycle.getId());
        assertEquals(LocalDate.of(2025, 1, 1), cycle.getStartDate());
        assertEquals(LocalDate.of(2025, 12, 31), cycle.getEndDate());
        assertEquals(CycleState.OPEN, cycle.getState());
        assertEquals(admin, cycle.getAdmin());
        assertNotNull(cycle.getEvaluations());
    }

    @Test
    void testSettersUpdateValues() {
        cycle.setStartDate(LocalDate.of(2026, 2, 2));
        assertEquals(LocalDate.of(2026, 2, 2), cycle.getStartDate());
        cycle.setEndDate(LocalDate.of(2026, 11, 30));
        assertEquals(LocalDate.of(2026, 11, 30), cycle.getEndDate());
        cycle.setState(CycleState.CLOSED);
        assertEquals(CycleState.CLOSED, cycle.getState());
    }

    @Test
    void testEvaluationsList() {
        List<AppraisalEntity> evaluations = new ArrayList<>();
        AppraisalEntity appraisal = new AppraisalEntity();
        evaluations.add(appraisal);
        cycle.setEvaluations(evaluations);
        assertEquals(1, cycle.getEvaluations().size());
    }
}
