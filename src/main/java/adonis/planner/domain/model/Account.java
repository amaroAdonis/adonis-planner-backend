package adonis.planner.domain.model;

import adonis.planner.domain.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name="accounts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Account {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false)
    private User user;
    @Column(nullable=false,length=120) private String name;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private AccountType type;
    @Column(nullable=false,length=3) private String currency="BRL";
    @Column(nullable=false,precision=14,scale=2) private BigDecimal openingBalance=BigDecimal.ZERO;
    @Column(nullable=false) private boolean isArchived=false;
    @CreationTimestamp @Column(nullable=false,updatable=false) private Instant createdAt;
    @UpdateTimestamp private Instant updatedAt;
}
