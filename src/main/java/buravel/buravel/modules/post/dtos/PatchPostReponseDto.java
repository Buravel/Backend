package buravel.buravel.modules.post.dtos;

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
public class PatchPostReponseDto {
    private Long id;
    private String postTitle;
    private Long price;
    private String category;
    private Float rating;
    private Double lat;
    private Double lng;
    private String location;
    @Lob
    private String memo;
    private List<PostTagResponseDto> postTagList = new ArrayList<>();
}