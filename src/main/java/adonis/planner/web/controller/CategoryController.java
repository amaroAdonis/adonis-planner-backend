package adonis.planner.web.controller;

import adonis.planner.domain.model.*;
import adonis.planner.service.*;
import adonis.planner.web.dto.CategoryDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService svc;
    private final SecurityUtils sec;

    @GetMapping
    public List<Category> list() { return svc.list(sec.current()); }

    @PostMapping
    public ResponseEntity<Category> create(@Valid @RequestBody CategoryDTO d) {
        Category c = Category.builder().user(sec.current()).name(d.getName()).type(d.getType()).build();
        return ResponseEntity.ok(svc.save(c));
    }
}
