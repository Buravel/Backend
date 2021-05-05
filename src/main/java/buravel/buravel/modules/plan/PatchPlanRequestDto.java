package buravel.buravel.modules.plan;

import buravel.buravel.modules.post.PostDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatchPlanRequestDto {
    @NotBlank
    private Long planId;
    @NotBlank
    private String planTitle;
    private String planImage;
    @NotBlank
    private boolean published;
    @NotBlank
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotBlank
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String planTag;

    private PostDto[][] postDtos;
}