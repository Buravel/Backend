package buravel.buravel.modules.plan;

import buravel.buravel.modules.post.PostDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanDto {

    @NotBlank
    private String planTitle;
    private String planImage;
    @Column(nullable = false)
    private boolean published;
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String planTag;

    private PostDto[][] postDtos;
}
