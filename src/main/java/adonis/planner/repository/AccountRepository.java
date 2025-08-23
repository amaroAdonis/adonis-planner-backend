package adonis.planner.repository;

import adonis.planner.domain.model.Account;
import adonis.planner.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);
}
