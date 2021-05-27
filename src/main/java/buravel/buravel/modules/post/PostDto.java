package buravel.buravel.modules.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    @NotBlank
    private String postTitle;
    @NotBlank
    private Long price;
    @Lob
    private String postImage;
    @NotBlank
    private String category;
    @NotBlank
    private Float rating;
    @NotBlank
    private Double lat;
    @NotBlank
    private Double lng;
    @NotBlank
    private String location;
    @Lob
    private String memo;

    private String tags;
}

// todo 필요하면 더 validation