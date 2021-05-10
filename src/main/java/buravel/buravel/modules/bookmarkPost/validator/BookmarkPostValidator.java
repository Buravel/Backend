package buravel.buravel.modules.bookmarkPost.validator;

import buravel.buravel.modules.bookmarkPost.BookmarkPost;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class BookmarkPostValidator implements Validator {
    // checking시 필요할거 같아서 우선 생성

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(BookmarkPost.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
    }
}
