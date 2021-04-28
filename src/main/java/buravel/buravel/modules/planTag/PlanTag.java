package buravel.buravel.modules.planTag;

import buravel.buravel.modules.plan.Plan;
import buravel.buravel.modules.tag.Tag;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanTag {
    @Id
    @GeneratedValue
    @Column(name = "plantag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;
}