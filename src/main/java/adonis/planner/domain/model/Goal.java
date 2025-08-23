package adonis.planner.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="goals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Goal {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false)
    private User user;
    @Column(nullable=false) private String name;
    @Column(nullable=false,precision=14,scale=2) private BigDecimal targetAmount;
    @Column(nullable=false,precision=14,scale=2) private BigDecimal currentAmount=BigDecimal.ZERO;
    private LocalDate targetDate;
    @CreationTimestamp @Column(nullable=false,updatable=false) private Instant createdAt;
    @UpdateTimestamp private Instant updatedAt;
}
