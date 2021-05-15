package buravel.buravel.modules.bookmark;

import buravel.buravel.modules.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
    //public Page<Bookmark> findAllByBookmarkManager_Id(Long account_id, Pageable pageable);

    List<Bookmark> findAllByBookmarkManager(Account account);

    Bookmark findByBookmarkTitleAndBookmarkManager(String title, Account account);

    void deleteAllByBookmarkManager(Account user);
}
