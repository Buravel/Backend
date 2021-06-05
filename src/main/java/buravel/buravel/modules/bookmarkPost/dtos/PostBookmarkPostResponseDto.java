package buravel.buravel.modules.bookmarkPost.dtos;

import buravel.buravel.modules.post.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostBookmarkPostResponseDto {
 // todo: 이름이 너무 긴데..
    // 원조 post 정보
    private Long originPost_id;
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
    @Lob
    private String memo;
    private Long originPlan_id;
}
