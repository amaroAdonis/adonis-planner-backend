package adonis.planner.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AuthLogin {
    @Email String email;
    @NotBlank String password;
}
