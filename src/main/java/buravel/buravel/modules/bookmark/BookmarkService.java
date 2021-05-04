package buravel.buravel.modules.bookmark;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.AccountResponseDto;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {
    private BookmarkRepository bookmarkRepository;
    private ModelMapper modelMapper;

    public Bookmark createBookmark(BookmarkDto bookmarkDto, Account account) {
        Bookmark bookmark = new Bookmark();
        bookmark.setBookmarkTitle(bookmarkDto.getBookmarkTitle());
        bookmark.setBookmarkManager(account);
        return bookmark;
    }

    public void deleteBookmark(Long bookmark_id) throws NotFoundException {
        if(bookmarkRepository.findById(bookmark_id).isEmpty())
            throw new NotFoundException("not found");
        bookmarkRepository.deleteById(bookmark_id);
    }

    public BookmarkResponseDto createBookmarkResponseDto(Bookmark bookmark,Account account){
        BookmarkResponseDto bookmarkResponseDto = modelMapper.map(bookmark,BookmarkResponseDto.class);
        AccountResponseDto accountResponseDto = createAccountResponseDto(account);
        bookmarkResponseDto.setAccountResponseDto(accountResponseDto);
        return bookmarkResponseDto;
    }

    private AccountResponseDto createAccountResponseDto(Account account) {
        return modelMapper.map(account,AccountResponseDto.class);
    }
}
