package buravel.buravel.modules.bookmarkPost.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkPostResponseDto {
    private Long id;
    private boolean checked;

    // 원조 post 정보
    private PostBookmarkPostResponseDto postBookmarkPostResponseDto;

    // 매핑된 plan.
    private Long planOf_id;

    // private BookmarkResponseDto bookmarkResponseDto;
    // 해당 북마크의 폴더
    private Long bookmark_id;
    // private String bookmark_title 필요할 시 title까지
    //todo 북마크파트에서 어떻게 무함참조 피할지, 필요한게 무엇인지 고민
}
