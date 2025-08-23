package adonis.planner.repository;

import adonis.planner.domain.model.RecurringTransaction;
import adonis.planner.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    List<RecurringTransaction> findByUserAndNextRunDateLessThanEqual(User user, LocalDate date);
}
