package buravel.buravel.modules.bookmarkPost;

import buravel.buravel.modules.bookmark.Bookmark;
import buravel.buravel.modules.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface BookmarkPostRepository extends JpaRepository<BookmarkPost,Long> {

    List<BookmarkPost> findByBookmarkAndChecked(Bookmark bookmark, boolean checked);
    boolean existsByBookmarkAndPostAndChecked(Bookmark bookmark, Post post, boolean checked);
    // todo: spring data jpa
}
