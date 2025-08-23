package adonis.planner.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="budgets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Budget {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false)
    private User user;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="category_id",nullable=false)
    private Category category;
    @Column(nullable=false) private LocalDate periodMonth;
    @Column(nullable=false,precision=14,scale=2) private BigDecimal limitAmount;
    @CreationTimestamp @Column(nullable=false,updatable=false) private Instant createdAt;
    @UpdateTimestamp private Instant updatedAt;
}
