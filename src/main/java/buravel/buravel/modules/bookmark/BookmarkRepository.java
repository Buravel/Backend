package buravel.buravel.modules.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
}
