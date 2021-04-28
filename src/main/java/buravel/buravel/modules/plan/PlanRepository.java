package buravel.buravel.modules.plan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface PlanRepository extends JpaRepository<Plan,Long> {
}
