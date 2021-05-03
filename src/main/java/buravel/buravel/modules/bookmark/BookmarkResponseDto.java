package buravel.buravel.modules.bookmark;

import buravel.buravel.modules.account.AccountResponseDto;
import buravel.buravel.modules.bookmarkPost.BookmarkPostResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponseDto {
    private Long id;
    private String bookmarkTitle;
    private AccountResponseDto accountResponseDto;
    private List<BookmarkPostResponseDto> bookmarkPostResponseDtos = new ArrayList<>();
    //todo 북마크파트에서 어떻게 무함참조 피할지, 필요한게 무엇인지 고민
}
