package adonis.planner.repository;

import adonis.planner.domain.model.Budget;
import adonis.planner.domain.model.Category;
import adonis.planner.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByUserAndCategoryAndPeriodMonth(User user, Category category, LocalDate periodMonth);
}
