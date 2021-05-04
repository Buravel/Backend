package buravel.buravel.modules.bookmarkPost;

import buravel.buravel.modules.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface BookmarkPostRepository extends JpaRepository<BookmarkPost,Long> {
}
