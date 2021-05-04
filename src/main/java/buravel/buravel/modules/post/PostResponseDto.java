package buravel.buravel.modules.post;

import buravel.buravel.modules.account.AccountResponseDto;
import buravel.buravel.modules.bookmarkPost.BookmarkPostResponseDto;
import buravel.buravel.modules.plan.PlanResponseDto;
import buravel.buravel.modules.postTag.PostTagResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String postTitle;
    private Long price;
    private String outputPrice;
    @Lob
    private String postImage;
    private PostCategory category;
    private Float rating;
    private boolean closed;
    private Double lat;
    private Double log;
    private Integer day;
    private Integer ordering;
    @Lob
    private String memo;
    private AccountResponseDto accountResponseDto;
    private PlanResponseDto planResponseDto;
    private List<PostTagResponseDto> postTagResponseDtoList = new ArrayList<>();
    private List<BookmarkPostResponseDto> bookmarkPostResponseDtos = new ArrayList<>();
    //todo 북마크파트에서 북마크 - post 응답용 dto해결해야 가능
}
