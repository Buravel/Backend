package buravel.buravel.modules.bookmark;

import buravel.buravel.modules.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
    //public Page<Bookmark> findAllByBookmarkManager_Id(Long account_id, Pageable pageable);

    public List<Bookmark> findAllByBookmarkManager_Id(Long account_id);

    public Bookmark findByBookmarkTitleAndBookmarkManager_Id(String title, Long account_id);

}
