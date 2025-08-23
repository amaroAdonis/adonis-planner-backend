package adonis.planner.repository;

import adonis.planner.domain.enums.CategoryType;
import adonis.planner.domain.model.Category;
import adonis.planner.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserAndType(User user, CategoryType type);
}