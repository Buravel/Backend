package buravel.buravel.modules.plan;

import buravel.buravel.modules.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface PlanRepository extends JpaRepository<Plan,Long>,PlanRepositoryExtension{
    List<Plan> findAllByPublishedOrderByLastModified(boolean b);

    @EntityGraph(value = "Plan.withAll",type = EntityGraph.EntityGraphType.FETCH)
    Page<Plan> findByPlanManagerAndPublished(Account user, boolean b, Pageable pageable);

    List<Plan> findAllByPlanManager(Account user);

    List<Plan> findByPlanManagerAndPublished(Account kiseok, boolean b);

    @EntityGraph(value = "Plan.withAll",type = EntityGraph.EntityGraphType.FETCH)
    Optional<Plan> findById(Long id);
}

