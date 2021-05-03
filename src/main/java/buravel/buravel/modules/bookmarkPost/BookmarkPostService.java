package buravel.buravel.modules.bookmarkPost;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkPostService {

    private final BookmarkPostRepository bookmarkPostRepository;
    private final AccountRepository accountRepository;

    public List<BookmarkPost> getBookmarkPosts(Long bookmarkId, Account account){
        Account user = accountRepository.findByEmail(account.getEmail());

        return bookmarkPostRepository.findAll(); // todo: jpa join 찾아서 실행
    }
}
