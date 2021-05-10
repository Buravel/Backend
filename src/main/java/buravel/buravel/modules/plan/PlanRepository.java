package buravel.buravel.modules.plan;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface PlanRepository extends JpaRepository<Plan,Long>,PlanRepositoryExtension{
    List<Plan> findAllByPublishedOrderByLastModified(boolean b);

}

