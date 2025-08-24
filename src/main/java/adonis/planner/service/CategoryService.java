package adonis.planner.service;

import adonis.planner.domain.model.Category;
import adonis.planner.domain.model.User;
import adonis.planner.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repo;

    public List<Category> list(User u) {
        return repo.findAll().stream().filter(c -> c.getUser().getId().equals(u.getId())).toList();
    }
    public Category get(User u, Long id) { return repo.findById(id).filter(c -> c.getUser().getId().equals(u.getId())).orElseThrow(); }
    public Category save(Category c) { return repo.save(c); }
    public void delete(User u, Long id) { repo.delete(get(u, id)); }
}
