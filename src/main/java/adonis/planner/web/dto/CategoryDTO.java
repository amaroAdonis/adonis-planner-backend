package adonis.planner.web.dto;

import adonis.planner.domain.enums.CategoryType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CategoryDTO {
    @NotBlank String name;
    @NotNull CategoryType type;
    Long parentId;
}
