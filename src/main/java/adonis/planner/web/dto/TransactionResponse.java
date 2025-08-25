package adonis.planner.web.dto;

import adonis.planner.domain.enums.TransactionType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
public class TransactionResponse {
    Long id;
    LocalDate date;
    Long accountId;
    Long categoryId;
    TransactionType type;
    BigDecimal amount;
    String description;
    String createdAt;
    String updatedAt;
}