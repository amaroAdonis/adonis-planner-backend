package adonis.planner.web.dto;

import adonis.planner.domain.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionDTO {
    @NotNull LocalDate date;
    @NotNull Long accountId;
    Long categoryId;
    @NotNull TransactionType type;
    @NotNull BigDecimal amount;
    String description;
}
