package buravel.buravel.modules.plan;

import buravel.buravel.modules.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface PlanRepositoryExtension {
    Page<Plan> findWithSearchCond(String keyword, Pageable pageable);

    Page<Plan> findWithSearchCondContainsPrice(String keyword, long min, long max, Pageable pageable);

    Plan findPlanSoon(Account account);
}
