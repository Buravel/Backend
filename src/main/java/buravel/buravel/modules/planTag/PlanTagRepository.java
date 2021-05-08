package buravel.buravel.modules.planTag;

import buravel.buravel.modules.plan.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface PlanTagRepository extends JpaRepository<PlanTag,Long> {
    List<PlanTag> findAllByPlan(Plan plan);
}
