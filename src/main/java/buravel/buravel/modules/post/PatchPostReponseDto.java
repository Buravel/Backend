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
public class PatchPostReponseDto {
    private Long id;
    private String postTitle;
    private Long price;
    private String category;
    private Float rating;
    private Double lat;
    private Double log;
    @Lob
    private String memo;
    private List<PostTagResponseDto> postTagList = new ArrayList<>();
}