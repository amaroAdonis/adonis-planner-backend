package adonis.planner.domain.model;

import adonis.planner.domain.enums.TransactionStatus;
import adonis.planner.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false)
    private User user;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="account_id",nullable=false)
    private Account account;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="category_id")
    private Category category;
    @Column(nullable=false) private LocalDate date;
    @Column(nullable=false,precision=14,scale=2) private BigDecimal amount;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private TransactionType type;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private TransactionStatus status=TransactionStatus.PAID;
    private String description;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="counterpart_account_id")
    private Account counterpartAccount;
    private Long linkedTransactionId;
    @CreationTimestamp @Column(nullable=false,updatable=false) private Instant createdAt;
    @UpdateTimestamp private Instant updatedAt;
}
