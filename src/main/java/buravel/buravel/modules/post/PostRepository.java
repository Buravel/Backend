package buravel.buravel.modules.post;

import buravel.buravel.modules.plan.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllByPlanOf(Plan plan);
}
