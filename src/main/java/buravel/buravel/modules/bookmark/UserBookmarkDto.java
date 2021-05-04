package buravel.buravel.modules.bookmark;

import buravel.buravel.modules.account.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Data
public class UserBookmarkDto {
    private BookmarkDto bookmarkDto;
    private Account account;
}
// validation 검사할 때 필요