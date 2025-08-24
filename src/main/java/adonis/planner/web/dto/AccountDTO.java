package adonis.planner.web.dto;

import adonis.planner.domain.enums.AccountType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountDTO {
    @NotBlank String name;
    @NotNull AccountType type;
    @NotBlank String currency;
    @NotNull BigDecimal openingBalance;
}
