package buravel.buravel.modules.post;

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
public class PostForPlanResponseDto {
    /**
     * plan - 여행계획에서 각각의 post정보를 보여줘야한다.
     * 무엇이 필요한가?
     * 이 plan의 대한 정보들
     * + 각각의 post에 대한 정보들 이 때 post의 bookmarkPost필드는 필요가 있는가? x
     * + post의 account필드가 필요가 있는가 ? x
     * */
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
    private Double lng;
    private String location;
    private Integer day;
    private Integer ordering;
    @Lob
    private String memo;
    private List<PostTagResponseDto> postTagResponseDtoList = new ArrayList<>();
}
