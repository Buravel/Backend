package buravel.buravel.modules.bookmark.dtos;

import buravel.buravel.modules.account.Account;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserBookmarkDto {
    private BookmarkDto bookmarkDto;
    private Account account;
}