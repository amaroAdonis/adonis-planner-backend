package adonis.planner.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AuthRegister {
    @NotBlank String name;
    @Email String email;
    @NotBlank String password;
}
