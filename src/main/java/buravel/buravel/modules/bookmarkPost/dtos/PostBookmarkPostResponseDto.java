package buravel.buravel.modules.bookmarkPost.dtos;

import buravel.buravel.modules.post.PostCategory;
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
public class PostBookmarkPostResponseDto {
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
    private Double lng;
    private String location;
    @Lob
    private String memo;
    private List<PostTagResponseDto> postTagResponseDtoList = new ArrayList<>();
    // 원조 plan id
    private Long originPlan_id;
}
