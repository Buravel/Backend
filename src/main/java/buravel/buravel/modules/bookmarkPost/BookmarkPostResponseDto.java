package buravel.buravel.modules.bookmarkPost;

import buravel.buravel.modules.bookmark.BookmarkResponseDto;
import buravel.buravel.modules.plan.PlanResponseDto;
import buravel.buravel.modules.post.PostResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkPostResponseDto {
    private Long id;
    private PostResponseDto postResponseDto;
    private boolean checked;
    private PlanResponseDto planResponseDto;
    private BookmarkResponseDto bookmarkResponseDto;
    //todo 북마크파트에서 어떻게 무함참조 피할지, 필요한게 무엇인지 고민
}
