package adonis.planner.repository;

import adonis.planner.domain.model.Goal;
import adonis.planner.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUser(User user);
}
