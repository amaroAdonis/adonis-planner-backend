package adonis.planner.repository;

import adonis.planner.domain.model.Transaction;
import adonis.planner.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserAndDateBetween(User user, LocalDate from, LocalDate to);

    @Query("SELECT COALESCE(SUM(CASE WHEN t.type = adonis.planner.domain.enums.TransactionType.INCOME THEN t.amount ELSE -t.amount END), 0) " +
            "FROM Transaction t WHERE t.account.id = :accountId AND t.user = :user")
    BigDecimal balanceDeltaByAccount(User user, Long accountId);

}


