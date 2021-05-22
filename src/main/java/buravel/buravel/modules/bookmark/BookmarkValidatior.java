package buravel.buravel.modules.bookmark;

import buravel.buravel.modules.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class BookmarkValidatior implements Validator {
    private final BookmarkRepository bookmarkRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return BookmarkDto.class.isAssignableFrom(aClass);
    }

    public void validate(Object target, Errors errors) {
        UserBookmarkDto userbookmarkDto = (UserBookmarkDto) target;
        Account account = userbookmarkDto.getAccount();
        String bookmarkTitle = userbookmarkDto.getBookmarkDto().getBookmarkTitle();
        Bookmark bookmark =  bookmarkRepository.findByBookmarkTitleAndBookmarkManager(bookmarkTitle, account);
        if (bookmark != null) {
            errors.rejectValue("bookmarkTitle", "wrongValue", "there is already same named bookmark");
        }
    }
}
