package adonis.planner.repository;

import adonis.planner.domain.model.Transaction;
import adonis.planner.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserAndDateBetween(User user, LocalDate from, LocalDate to);
}
